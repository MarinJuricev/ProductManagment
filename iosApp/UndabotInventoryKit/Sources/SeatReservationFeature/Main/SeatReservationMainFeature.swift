import ComposableArchitecture
import Core
import Dependencies
import Utilities
import CommonUI

@Reducer
public struct SeatReservationMainFeature {

    @Dependency(\.seatReservationDashboardTypeClient) var dashboardClient

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var user: IAUser
        var screenType: LoadableState<ScreenType.State>
        @Presents var destination: Destination.State?

        public init(
            user: IAUser,
            screenType: LoadableState<ScreenType.State> = .loading,
            destination: Destination.State? = nil
        ) {
            self.user = user
            self.screenType = screenType
            self.destination = destination
        }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case view(View)
        case binding(BindingAction<State>)
        case screenType(ScreenType.Action)
        case dashboardResponse(Result<IASeatReservationDashboardType, Error>)
        case destination(PresentationAction<Destination.Action>)
        case currentUserChanged(IAUser)

        public enum View: Sendable {
            case onTask
            case onRetryButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return getDashboard(state: &state)

            case .view(.onRetryButtonTapped):
                return getDashboard(state: &state)
            case .dashboardResponse(.success(let dashboardType)):
                switch dashboardType {
                case .administrator(let options):
                    state.screenType = .loaded(.dashboard(DashboardFeature<IASeatReservationOption>.State(options: options)))
                case .user:
                    state.screenType = .loaded(.timeline(SeatReservationFeature.State(currentUserId: state.user.id)))
                }
                return .none

            case .currentUserChanged(let updatedUser):
                let oldRole = state.user.role
                state.user = updatedUser
                guard oldRole != updatedUser.role else {
                    return .none
                }
                state.destination = nil
                return getDashboard(state: &state)

            case .dashboardResponse(.failure):
                state.screenType = .failed
                return .none

            case .screenType(.dashboard(.delegate(.optionTapped(let seatReservationOption)))):
                switch seatReservationOption {
                case .seatManagement:
                    state.destination = .seatManagement(SeatManagementFeature.State())
                case .timeline:
                    state.destination = .timeline(SeatReservationFeature.State(userId: state.user.id))
                }
                return .none

            case .screenType:
                return .none

            case .destination:
                return .none

            case .binding:
                return .none
            }
        }
        .ifLet(\.screenType.loaded, action: \.screenType) {
            ScreenType.body
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func getDashboard(state: inout State) -> EffectOf<Self> {
        state.screenType = .loading
        return .run { [user = state.user] send in
            await send(.dashboardResponse(Result { try await dashboardClient.get(for: user)}))
        }
    }
}

extension SeatReservationMainFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case seatManagement(SeatManagementFeature)
        case timeline(SeatReservationFeature)
    }
}

extension SeatReservationMainFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum ScreenType {
        case dashboard(DashboardFeature<IASeatReservationOption>)
        case timeline(SeatReservationFeature)
    }
}
