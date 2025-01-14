import ComposableArchitecture
import Shared
import Utilities
import Foundation
import SwiftUI
import Core

@Reducer
public struct UserRequestDetailsFeature: Sendable {

    @Dependency(\.parkingReservationsClient.submitUpdatedReservation) var submitUpdatedReservation
    @Dependency(\.userRequestDetailsErrorMapper.map) var mapToAlertState
    @Dependency(\.dismiss) var dismiss

    @ObservableState
    public struct State: Equatable, Sendable {
        var parkingReservation: IAParkingReservation
        @Shared var status: IAFlexibleParkingReservationStatus
        let initialStatus: IAFlexibleParkingReservationStatus
        @Presents var destination: Destination.State?
        var isRequestInFlight: Bool
        var reservationApproval: ReservationApprovalFeature.State?
        var focus: Field?

        public init(parkingReservation: IAParkingReservation) {
            let sharedStatus = Shared(IAFlexibleParkingReservationStatus(parkingReservation.status))
            self.parkingReservation = parkingReservation
            self._status = sharedStatus
            self.destination = nil
            self.isRequestInFlight = false
            self.focus = nil
            self.initialStatus = IAFlexibleParkingReservationStatus(parkingReservation.status)
            if let sharedParkingCoordinate = sharedStatus.approved.optional?.parkingCoordinate {
                self.reservationApproval = ReservationApprovalFeature.State(
                    email: Shared(parkingReservation.email),
                    date: Shared(parkingReservation.date),
                    parkingCoordinate: sharedParkingCoordinate,
                    isGarageAccessAleardyGiven: parkingReservation.status.is(\.approved),
                    userRequestId: parkingReservation.id)
            }
        }

        public init(parkingReservation: IAParkingReservation,
                    garageLevelsData: LoadableState<[IAGarageLevelData]>,
                    destination: Destination.State?,
                    isRequestInFlight: Bool,
                    focus: Field?,
                    reservationApproval: ReservationApprovalFeature.State? = nil
        ) {
            let sharedStatus = Shared(IAFlexibleParkingReservationStatus(parkingReservation.status))
            self.parkingReservation = parkingReservation
            self._status = sharedStatus
            self.destination = destination
            self.isRequestInFlight = isRequestInFlight
            self.focus = focus
            self.initialStatus = IAFlexibleParkingReservationStatus(parkingReservation.status)
            self.reservationApproval = reservationApproval
        }

        public enum Field: Sendable {
            case adminNote
        }

        var shouldShowParkingCoordinates: Bool {
            status.is(\.approved)
        }

        var shouldShowSaveButton: Bool {
            guard initialStatus != status else { return false }
            switch status {
            case .submitted, .declined:
                return true
            case .approved:
                return status.approved?.parkingCoordinate != nil
            case .cancelled:
                return false
            }
        }

        var isSaveButtonDisabled: Bool {
            status.is(\.approved) && reservationApproval?.isValid == false
        }

        var shouldShowAdminNote: Bool {
            !status.is(\.submitted)
        }

        var userRequestStatus: IAParkingReservationStatusType {
            get { status.toParkingReservationStatusType() }
            set { 
                guard newValue != status.toParkingReservationStatusType() else { return }
                status = newValue.toReservationStatus()
            }
        }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case view(ViewAction)
        case binding(BindingAction<State>)
        case destination(PresentationAction<Destination.Action>)
        case userRequestUpdateResponse(Result<Void, Error>)
        case reservationApproval(ReservationApprovalFeature.Action)
        case statusChanged(IAFlexibleParkingReservationStatus)

        public enum ViewAction: Sendable {
            case saveButtonTapped
            case onTask
            case hideKeyboard
        }

        public enum Alert: Sendable {
            case successConfirmation
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.hideKeyboard):
                state.focus = nil
                return .none

            case .view(.onTask):
                return .publisher {
                    state.$status.publisher.map(Action.statusChanged)
                }

            case .statusChanged(let newStatus):
                return handleParkingCoordinates(state: &state, newStatus: newStatus)

            case .view(.saveButtonTapped):
                guard let parkingReservationStatus = state.status.toParkingReservationStatus() else {
                    return .none
                }
                state.focus = nil
                state.parkingReservation.status = parkingReservationStatus
                state.isRequestInFlight = true
                return .run { [parkingReservation = state.parkingReservation] send in
                    await send(.userRequestUpdateResponse(Result { try await
                        submitUpdatedReservation(parkingReservation) }))
                }

            case .userRequestUpdateResponse(.success):
                state.isRequestInFlight = false
                state.destination = .alert(.success)
                return .none

            case .userRequestUpdateResponse(.failure(let error)):
                state.isRequestInFlight = false
                state.destination = .alert(mapToAlertState(error))
                return .none

            case .destination(.presented(.alert(.successConfirmation))):
                return .run { _ in
                    await dismiss()
                }
            case .destination:
                return .none
            case .binding:
                return .none

            case .reservationApproval:
                return .none
            }
        }
        .ifLet(\.reservationApproval, action: \.reservationApproval) {
            ReservationApprovalFeature()
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func handleParkingCoordinates(state: inout State, newStatus: IAFlexibleParkingReservationStatus) -> EffectOf<Self> {
        if let sharedParkingCoordinate = state.$status.approved.optional?.parkingCoordinate,
            state.reservationApproval == nil {
            state.reservationApproval = ReservationApprovalFeature.State(
                email: Shared(state.parkingReservation.email),
                date: Shared(state.parkingReservation.date),
                parkingCoordinate: sharedParkingCoordinate,
                isGarageAccessAleardyGiven: state.initialStatus.is(\.approved),
                userRequestId: state.parkingReservation.id)
        }
        return .none
    }
}

extension UserRequestDetailsFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        @ReducerCaseEphemeral
        case alert(AlertState<UserRequestDetailsFeature.Action.Alert>)
    }
}

extension UserRequestDetailsFeature {
    public enum Request: Sendable {
        case all
        case user
        case garageLevels
    }
}

extension AlertState where Action == UserRequestDetailsFeature.Action.Alert {
    static let success = AlertState {
        TextState(resource: \.user_request_status_change_success)
    } actions: {
        ButtonState(action: .successConfirmation) {
            TextState(resource: \.general_ok)
        }
    }

    static let invalidParkingCoordinate = AlertState {
        TextState(resource: \.user_request_invalid_parking_coordinate)
    }
}

extension IAParkingReservationStatusType {
    init(_ status: UserRequestDetailsFeature.IAFlexibleParkingReservationStatus) {
        switch status {
        case .cancelled:
            self = .cancelled
        case .submitted:
            self = .submitted
        case .approved:
            self = .approved
        case .declined:
            self = .declined
        }
    }

    func toReservationStatus() -> UserRequestDetailsFeature.IAFlexibleParkingReservationStatus {
        switch self {
        case .cancelled:
            return .cancelled
        case .submitted:
            return .submitted
        case .approved:
            return .approved(adminNote: "", parkingCoordinate: nil)
        case .declined:
            return .declined(adminNote: "")
        }
    }
}
