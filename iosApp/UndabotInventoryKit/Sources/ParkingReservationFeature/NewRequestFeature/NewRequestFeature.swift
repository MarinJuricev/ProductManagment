@preconcurrency import Shared
import ComposableArchitecture
import Foundation
import Dependencies
import Core
import Utilities
import SwiftUI

@Reducer
public struct NewRequestFeature: Sendable {
    public init() {}

    @Dependency(\.parkingReservationsClient) var parkingReservationClient
    @Dependency(\.newRequestErrorMapper.map) var mapToAlertState
    @Dependency(\.garageLevelsClient) var garageLevelsClient
    @Dependency(\.dismiss) var dismiss

    @ObservableState
    public struct State: Equatable, Sendable {
        @Presents var destination: Destination.State?
        @Shared var user: IAUser
        @Shared var date: Date
        var currentUser: IAUser
        var note: String
        var isRequestInFlight: Bool
        var focus: Field?
        var isDatePickerVisible: Bool = false
        var reservationApproval: ReservationApprovalFeature.State?
        var multiDatePicker: MultiDatePickerFeature.State?

        var isReservation: Bool {
            @Dependency(\.userClient.isUserAbleToMakeReservation) var isUserAbleToMakeReservation
            return isUserAbleToMakeReservation(currentUser)
        }

        var shouldShowGarageAccessToggle: Bool {
            user.hasPermanentGarageAccess == false
        }

        var isSubmitButtonDisabled: Bool {
            reservationApproval?.isValid == false || multiDatePicker?.isValid == false
        }

        public init(
            user: IAUser
        ) {
            let sharedUser = Shared(user)
            self._user = sharedUser
            self.currentUser = user
            self.note = ""
            @Dependency(\.date.now) var date
            let tomorrow = date.tomorrow()
            let sharedDate = Shared(tomorrow)
            self._date = sharedDate
            self.destination = nil
            self.focus = nil
            self.isRequestInFlight = false
            if isReservation {
                self.reservationApproval = ReservationApprovalFeature.State(email: sharedUser.email, date: sharedDate)
            } else {
                self.multiDatePicker = MultiDatePickerFeature.State(startDate: tomorrow)
            }
        }

        public init(
            user: IAUser,
            currentUser: IAUser,
            note: String = "",
            destination: Destination.State? = nil,
            isRequestInFlight: Bool = false,
            focus: Field? = nil
        ) {
            self._user = Shared(user)
            self.currentUser = currentUser
            self.note = note
            @Dependency(\.date.now) var date
            self._date = Shared(date.tomorrow())
            self.destination = destination
            self.isRequestInFlight = isRequestInFlight
            self.focus = focus
        }

        public enum Field: Equatable, Sendable {
            case adminNote
        }
    }

    public enum Action: BindableAction, ViewAction, Sendable {
        case parkingReservationResponse(Result<Void, Error>)
        case binding(BindingAction<State>)
        case view(ViewAction)
        case destination(PresentationAction<Destination.Action>)
        case userChanged(IAUser)
        case reservationApproval(ReservationApprovalFeature.Action)
        case multiDatePicker(MultiDatePickerFeature.Action)

        @CasePathable
        public enum Alert: Equatable, Sendable {
            case successConfirmation
        }

        @CasePathable
        public enum ViewAction: Sendable {
            case submitTapped
            case hideKeyboard
            case onUserPickerTapped
            case onTask
            case datePickerTapped
        }
    }

    public var body: some ReducerOf<Self> {
        ReducerReader { state, _ in
            BindingReducer()

            Reduce { state, action in
                switch action {
                case .view(.datePickerTapped):
                    state.isDatePickerVisible = true
                    return .none
                case .view(.submitTapped):
                    return submitParkingReservationRequest(state: &state)

                case .view(.hideKeyboard):
                    state.focus = nil
                    return .none

                case .view(.onUserPickerTapped):
                    guard state.isReservation else {
                        return .none
                    }

                    state.destination = .usersPicker(UsersPickerFeature.State(selectedUser: state.$user))
                    return .none

                case .view(.onTask):
                    return .publisher {
                        state.$user.publisher.map(Action.userChanged)
                    }

                case .userChanged:
                    guard state.isReservation else {
                        return .none
                    }

                    if state.reservationApproval == nil {
                        state.reservationApproval = ReservationApprovalFeature.State(email: state.$user.email, date: state.$date)
                    }
                    return .none

                case .parkingReservationResponse(.success):
                    state.isRequestInFlight = false
                    state.destination = .alert(.successfulRequest)
                    return .none

                case .parkingReservationResponse(.failure(let error)):
                    state.isRequestInFlight = false
                    state.destination = .alert(mapToAlertState(error))
                    return .none

                case .binding(\.date):
                    state.isDatePickerVisible = false
                    return .none

                case .binding:
                    return .none

                case .destination(.presented(.multiRequestResponse(.delegate(.onDoneButtonTapped)))):
                    return .run { _ in
                        await dismiss()
                    }

                case .destination(.presented(.alert(.successConfirmation))):
                    return .run { _ in
                        await dismiss()
                    }
                case .destination:
                    return .none
                case .reservationApproval:
                    return .none
                case .multiDatePicker:
                    return .none
                }
            }
            .ifLet(\.$destination, action: \.destination)
            .ifLet(\.reservationApproval, action: \.reservationApproval) {
                ReservationApprovalFeature()
                    .dependency(\.userClient.getUser, { @Sendable _ in
                        state.user
                    })
            }
            .ifLet(\.multiDatePicker, action: \.multiDatePicker) {
                MultiDatePickerFeature()
            }
        }
    }

    private func submitParkingReservationRequest(state: inout State) -> EffectOf<Self> {
        state.focus = nil

        if state.isReservation {
            guard let parkingReservations = prepareParkingReservation(state: state) else { return .none }
            state.isRequestInFlight = true
            return .run { send in
                await send(.parkingReservationResponse(Result { try await parkingReservationClient.createReservation(parkingReservations) }))
            }
        } else {
            guard let parkingRequests = prepareParkingRequests(state: state) else { return .none }
            state.destination = .multiRequestResponse(MultiRequestResponseFeature.State(parkingRequests: parkingRequests))
            return .none
        }
    }

    private func prepareParkingReservation(state: State) -> ParkingRequest? {
        guard let parkingCoordinate = state.reservationApproval?.parkingCoordinate else { return nil }
        let timestamp = Int64(state.date.millisecondsSince1970)
        let email = state.user.email
        let note = state.note
        return ParkingRequest.Reservation(email: email, date: timestamp, adminNote: note, parkingCoordinate: ParkingCoordinate(parkingCoordinate))
    }

    private func prepareParkingRequests(state: State) -> [ParkingRequest]? {
        guard let dates = state.multiDatePicker?.dates else { return nil }
        let email = state.user.email
        let note = state.note
        return dates.compactMap { dateComponent in
            guard let date = dateComponent.date else { return nil }
            let timestamp = Int64(date.millisecondsSince1970)
            return ParkingRequest.Request(email: email, date: timestamp, note: note)
        }
    }
}

extension NewRequestFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        @ReducerCaseEphemeral
        case alert(AlertState<NewRequestFeature.Action.Alert>)
        case usersPicker(UsersPickerFeature)
        case multiRequestResponse(MultiRequestResponseFeature)
    }
}

extension AlertState where Action == NewRequestFeature.Action.Alert {
    static let successfulRequest = AlertState {
        TextState(resource: \.parking_reservation_success)
    } actions: {
        ButtonState(action: .successConfirmation) {
            TextState(resource: \.general_ok)
        }
    }
}
