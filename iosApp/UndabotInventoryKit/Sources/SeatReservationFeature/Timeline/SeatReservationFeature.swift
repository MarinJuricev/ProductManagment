import ComposableArchitecture
import Utilities
import Core

@Reducer
public struct SeatReservationFeature {

    @Dependency(\.reservableDatesClient) var reservableDatesClient
    @Dependency(\.officesClient) var officesClient

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var reservableDatesData: LoadableState<ReservableDatesFeature.State>
        var officesData: LoadableState<IdentifiedArrayOf<IAOffice>>
        var currentUserId: String
        var selectedOffice: IAOffice?

        public init(userId: String) {
            self.currentUserId = userId
            self.reservableDatesData = .loading
            self.officesData = .loading
            self.selectedOffice = nil
        }

        public init(
            reservableDatesData: LoadableState<ReservableDatesFeature.State> = .loading,
            officesData: LoadableState<IdentifiedArrayOf<IAOffice>> = .loading,
            currentUserId: String,
            selectedOffice: IAOffice? = nil
        ) {
            self.reservableDatesData = reservableDatesData
            self.officesData = officesData
            self.currentUserId = currentUserId
            self.selectedOffice = selectedOffice
        }
    }

    public enum Action: BindableAction, ViewAction, Sendable {
        case view(View)
        case binding(BindingAction<State>)
        case reservableDatesResponse(Result<[IAReservableDate], Error>)
        case officesResponse(Result<[IAOffice], Error>)
        case reservableDates(ReservableDatesFeature.Action)

        public enum View: Sendable {
            case onTask
            case retryOfficesButtonTapped
            case retryReservableDatesButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return getOffices(state: &state)

            case .view(.retryOfficesButtonTapped):
                return getOffices(state: &state)

            case .view(.retryReservableDatesButtonTapped):
                return observeReservableDates(state: &state)

            case .officesResponse(.success(let offices)):
                state.officesData = .loaded(IdentifiedArrayOf(uniqueElements: offices))
                state.selectedOffice = offices.first
                return observeReservableDates(state: &state)

            case .officesResponse(.failure):
                state.officesData = .failed
                return .none

            case .reservableDatesResponse(.success(let reservableDates)):
                guard let office = state.selectedOffice else {
                    return .none
                }
                if state.reservableDatesData.is(\.loaded) {
                    updateReservableDates(state: &state, reservableDates: reservableDates)
                } else {

                    state.reservableDatesData = .loaded(ReservableDatesFeature.State(
                        // swiftlint:disable:next line_length
                        reservableDates: IdentifiedArray(uniqueElements: reservableDates.map({ ReservableDateFeature.State(reservableDate: $0, currentUserId: state.currentUserId, officeId: office.id) }))
                    ))
                }
                return .none
            case .reservableDatesResponse(.failure):
                state.reservableDatesData = .failed
                return .none

            case .reservableDates:
                return .none
            case .officesResponse:
                return .none
            case .binding(\.selectedOffice):
                return observeReservableDates(state: &state)

            case .binding:
                return .none
            }
        }
        .ifLet(\.reservableDatesData.loaded, action: \.reservableDates) {
            ReservableDatesFeature()
        }
    }

    private func getOffices(state: inout State) -> EffectOf<Self> {
        state.officesData = .loading
        state.reservableDatesData = .loading
        return .run { send in
            await send(.officesResponse(Result { try await officesClient.get() }))
        }
    }

    private func observeReservableDates(state: inout State) -> EffectOf<Self> {
        guard let selectedOfficeId = state.selectedOffice?.id else { return .none }
        state.reservableDatesData = .loading
        return .run { send in
            for try await reservableDates in try reservableDatesClient.observe(selectedOfficeId) {
                await send(.reservableDatesResponse(.success(reservableDates)))
            }
        } catch: { error, send in
            await send(.reservableDatesResponse(.failure(error)))
        }
    }

    private func updateReservableDates(state: inout State, reservableDates: [IAReservableDate]) {
        guard let office = state.selectedOffice else {
            return
        }
        let reservableDateFeatures = IdentifiedArray(
            uniqueElements: reservableDates.map {
                let oldReservableDateState = state.reservableDatesData.loaded?.reservableDates[id: $0.id]
                var isRequestInFlight = oldReservableDateState?.isRequestInFlight ?? false
                let oldSeatHolders = oldReservableDateState?.reservableDate.weekday?.seatHolders ?? []
                let newSeatHolders = $0.weekday?.seatHolders ?? []
                if oldSeatHolders != newSeatHolders {
                    isRequestInFlight = false
                }
                return ReservableDateFeature.State(
                    reservableDate: $0,
                    currentUserId: state.currentUserId,
                    isRequestInFlight: isRequestInFlight,
                    officeId: office.id
                )
            }
        )
        state.reservableDatesData.modify(\.loaded) { $0.reservableDates = reservableDateFeatures }
    }
}
