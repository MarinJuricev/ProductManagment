import ComposableArchitecture
import Shared
import Foundation
import Utilities
import Core

@Reducer
public struct MyReservationDetailsFeature: Sendable {

    @Dependency(\.cancelRequestClient) var cancelRequestClient
    @Dependency(\.cancelMyReservationErrorMapper.map) var mapToAlertState
    @Dependency(\.dismiss) var dismiss

    @ObservableState
    public struct State: Equatable, Sendable {
        var parkingReservation: IAParkingReservation
        var isRequestInFlight: Bool
        @Presents var destination: Destination.State?

        public init(parkingReservation: IAParkingReservation,
                    isRequestInFlight: Bool = false,
                    destination: Destination.State? = nil
        ) {
            self.parkingReservation = parkingReservation
            self.isRequestInFlight = isRequestInFlight
            self.destination = destination
        }

        var submissionNote: String? {
            switch parkingReservation.status {
            case let .approved(adminNote, _):
                adminNote
            case .cancelled:
                nil
            case .declined(let adminNote):
                adminNote
            case .submitted:
                nil
            }
        }

        var shouldShowCancelButton: Bool {
            @Dependency(\.date.now) var now
            return parkingReservation.date.startOfDay() >= now.startOfDay() && (parkingReservation.status.is(\.approved) || parkingReservation.status.is(\.submitted))
        }
        
        var parkingCoordinate: IAParkingCoordinate? {
            guard let approvedStatus = parkingReservation.status[dynamicMember: \.approved] else { return nil }
            return approvedStatus.parkingCoordinate
        }
    }

    public enum Action: BindableAction, ViewAction, Sendable {
        case view(ViewAction)
        case binding(BindingAction<State>)
        case destination(PresentationAction<Destination.Action>)
        case cancelRequestResponse(Result<Void, Error>)

        @CasePathable
        public enum ViewAction: Sendable {
            case cancelButtonTapped
        }

        public enum Alert: Sendable {
            case cancelConfirmation
            case successConfirmation
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.cancelButtonTapped):
                state.destination = .alert(.cancelConfirmation(date: state.parkingReservation.date))
                return .none

            case .cancelRequestResponse(.success):
                state.isRequestInFlight = false
                state.destination = .alert(.success)
                return .none

            case .cancelRequestResponse(.failure(let error)):
                state.isRequestInFlight = false
                state.destination = .alert(mapToAlertState(error))
                return .none

            case .destination(.presented(.alert(.cancelConfirmation))):
                state.isRequestInFlight = true
                return .run { [parkingReservation = state.parkingReservation] send in
                    await send(.cancelRequestResponse(Result { try await cancelRequestClient.cancel(for: parkingReservation) }))
                }

            case .destination(.presented(.alert(.successConfirmation))):
                return .run { _ in
                    await dismiss()
                }
                
            case .destination:
                return .none
            case .binding:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }
}

extension MyReservationDetailsFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        @ReducerCaseEphemeral
        case alert(AlertState<MyReservationDetailsFeature.Action.Alert>)
    }
}

extension AlertState where Action == MyReservationDetailsFeature.Action.Alert {
    static let success = AlertState {
        TextState(resource: \.my_parking_reservation_cancel_request_success)
    } actions: {
        ButtonState(action: .successConfirmation) {
            TextState(resource: \.general_ok)
        }
    }

    static func cancelConfirmation(date: Date) -> AlertState {
        AlertState {
            TextState(generalCancelQuestion(input: date.format(using: .numericDottedFormatter)).localized())
        } actions: {
            ButtonState {
                TextState(resource: \.my_parking_reservation_cancel_confirmation_dismiss)
            }
            ButtonState(action: .cancelConfirmation) {
                TextState(resource: \.my_parking_reservation_cancel_confirmation_accept)
            }
        } message: {
            TextState(myParkingReservationCancelConfirmationTitle(input: date.format(using: .numericDottedFormatter)).localized())
        }
    }
}
