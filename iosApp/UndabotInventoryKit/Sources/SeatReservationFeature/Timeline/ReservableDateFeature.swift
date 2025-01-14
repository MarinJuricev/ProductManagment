import ComposableArchitecture
import Core
import Foundation

@Reducer
public struct ReservableDateFeature {
    
    @Dependency(\.reservableDatesClient) var reservableDatesClient

    @ObservableState
    public struct State: Equatable, Sendable, Identifiable {
        public var id: Date {
            reservableDate.id
        }

        var reservableDate: IAReservableDate
        var isRequestInFlight: Bool
        var currentUserId: String
        var officeId: String

        init(
            reservableDate: IAReservableDate,
            currentUserId: String,
            isRequestInFlight: Bool = false,
            officeId: String
        ) {
            self.reservableDate = reservableDate
            self.isRequestInFlight = isRequestInFlight
            self.currentUserId = currentUserId
            self.officeId = officeId
        }

        var isAlreadyReserved: Bool {
            reservableDate.weekday?.seatHolders.contains(where: { $0.id == currentUserId }) ?? false
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(View)
        case reserveSeatResponse(Result<Void, Error>)
        case cancelReservationResponse(Result<Void, Error>)

        public enum View: Sendable {
            case reserveButtonTapped
            case cancelReservationButton
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(.reserveButtonTapped):
                return reserve(state: &state)
            case .view(.cancelReservationButton):
                return cancelReservation(state: &state)
            case .reserveSeatResponse(.success):
                state.isRequestInFlight = false
                return .none
            case .reserveSeatResponse(.failure):
                state.isRequestInFlight = false
                return .none
            case .cancelReservationResponse(.success):
                state.isRequestInFlight = false
                return .none
            case .cancelReservationResponse(.failure):
                state.isRequestInFlight = false
                return .none
            }
        }
    }

    private func reserve(state: inout State) -> EffectOf<Self> {
        guard let date = state.reservableDate.weekday?.date else {
            return .none
        }
        state.isRequestInFlight = true
        return .run { [officeId = state.officeId] send in
            await send(.reserveSeatResponse(Result { try await reservableDatesClient.reserveSeat(officeId, date) }))
        }
    }

    private func cancelReservation(state: inout State) -> EffectOf<Self> {
        guard let date = state.reservableDate.weekday?.date else {
            return .none
        }
        state.isRequestInFlight = true
        return .run { [officeId = state.officeId] send in
            await send(.cancelReservationResponse(Result { try await reservableDatesClient.cancelReservation(officeId, date) }))
        }
    }
}
