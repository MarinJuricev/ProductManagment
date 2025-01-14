import ComposableArchitecture
import Core
import Utilities
import Foundation

@Reducer
public struct ReservationApprovalFeature {

    @Dependency(\.garageLevelsClient) var garageLevelsClient
    @Dependency(\.userClient) var userClient

    @ObservableState
    public struct State: Equatable, Sendable {
        @Shared var parkingCoordinate: IAParkingCoordinate?
        @Shared var email: String
        @Shared var date: Date
        var userRequestId: String?
        @Presents var parkingCoordinatePickerFeature: ParkingCoordinatePickerFeature.State?
        var garageLevelsData: LoadableState<[IAGarageLevelData]> = .initial
        var userData: LoadableState<IAUser> = .initial
        var hasGarageAccess: Bool
        var isGarageAccessAlreadyGiven: Bool

        var isValid: Bool {
            hasGarageAccess && parkingCoordinate != nil
        }

        var isRequestInFlight: Bool {
            garageLevelsData.is(\.loading) || userData.is(\.loading)
        }

        var shouldShowRetryButton: Bool {
            garageLevelsData.is(\.failed) || userData.is(\.failed)
        }

        var shouldShowGarageAccessToggle: Bool {
            userData.loaded?.hasPermanentGarageAccess == false && isGarageAccessAlreadyGiven == false
        }

        init(
            email: Shared<String>,
            date: Shared<Date>,
            parkingCoordinate: Shared<IAParkingCoordinate?> = Shared<IAParkingCoordinate?>(nil),
            isGarageAccessAleardyGiven: Bool = false,
            userRequestId: String? = nil
        ) {
            self._parkingCoordinate = parkingCoordinate
            self._email = email
            self._date = date
            self.parkingCoordinatePickerFeature = nil
            self.garageLevelsData = .loading
            self.userData = .loading
            self.hasGarageAccess = isGarageAccessAleardyGiven
            self.isGarageAccessAlreadyGiven = isGarageAccessAleardyGiven
            self.userRequestId = userRequestId
        }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case parkingCoordinatePickerFeature(ParkingCoordinatePickerFeature.Action)
        case garageLevelsResponse(Result<[IAGarageLevelData], Error>)
        case userResponse(Result<IAUser, Error>)
        case dateChanged(Date)
        case emailChanged(String)
        case view(View)
        case binding(BindingAction<State>)

        public enum View: Sendable {
            case onTask
            case onRetryButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return .merge(
                    fetchGarageLevels(state: &state),
                    fetchUser(state: &state),
                    .publisher {
                        state.$date.publisher.map(Action.dateChanged)
                    },
                    .publisher {
                        state.$email.publisher.map(Action.emailChanged)
                    }
                )
            case .view(.onRetryButtonTapped):
                return handleRetryButtonTapped(state: &state)

            case .dateChanged:
                state.parkingCoordinate = nil
                return fetchGarageLevels(state: &state)

            case .emailChanged:
                return fetchUser(state: &state)

            case .userResponse(.success(let user)):
                state.userData = .loaded(user)
                state.hasGarageAccess = user.hasPermanentGarageAccess || state.isGarageAccessAlreadyGiven
                return .none
            case .userResponse(.failure):
                state.userData = .failed
                return .none
            case .garageLevelsResponse(.success(let garageLevelData)):
                state.garageLevelsData = .loaded(garageLevelData)
                if garageLevelData.isEmpty == false {
                    state.parkingCoordinatePickerFeature = ParkingCoordinatePickerFeature.State(garageLevels: garageLevelData, parkingCoordinate: state.$parkingCoordinate)
                } else {
                    state.parkingCoordinatePickerFeature = nil
                }
                return .none
            case .garageLevelsResponse(.failure):
                state.garageLevelsData = .failed
                return .none
            case .parkingCoordinatePickerFeature:
                return .none
            case .binding:
                return .none
            }
        }
        .ifLet(\.parkingCoordinatePickerFeature, action: \.parkingCoordinatePickerFeature) {
            ParkingCoordinatePickerFeature()
        }
    }

    private func fetchGarageLevels(state: inout State) -> EffectOf<Self> {
        state.garageLevelsData = .loading
        return .run { [date = state.date, id = state.userRequestId] send in
            await send(.garageLevelsResponse(Result { try await garageLevelsClient.getEmptyLevels(for: date, id: id)}))
        }
    }

    private func fetchUser(state: inout State) -> EffectOf<Self> {
        state.userData = .loading
        return .run { [email = state.email] send in
            await send(.userResponse(Result { try await userClient.getUser(email) }))
        }
    }

    private func handleRetryButtonTapped(state: inout State) -> EffectOf<Self> {
        state.parkingCoordinatePickerFeature = nil
        if state.garageLevelsData.is(\.failed),
           state.userData.is(\.failed) {
            return .merge(
                fetchGarageLevels(state: &state),
                fetchUser(state: &state)
            )
        } else if state.garageLevelsData.is(\.failed) {
            return fetchGarageLevels(state: &state)
        } else if state.userData.is(\.failed) {
            return fetchUser(state: &state)
        } else {
            return .none
        }
    }
}

extension ReservationApprovalFeature {
    public enum Request: Sendable {
        case all
        case user
        case garageLevels
    }
}
