import ComposableArchitecture
import Core
import CommonUI
import Shared
import Utilities

@Reducer
public struct OfficeFormFeature {

    @Dependency(\.officesClient.edit) var editOffice
    @Dependency(\.dismiss) var dismiss
    private let validateEditing: @Sendable (IAOffice, String, String) -> Bool
    private let validateTitle: @Sendable (IAOffice, String) -> String?

    public init(validateEditing: @Sendable @escaping (IAOffice, String, String) -> Bool,
                validateTitle: @Sendable @escaping (IAOffice, String) -> String?
    ) {
        self.validateEditing = validateEditing
        self.validateTitle = validateTitle
    }

    @ObservableState
    public struct State: Equatable, Sendable {
        @Shared var office: IAOffice
        var officeTitleTextField: ValidatableTextFieldFeature.State
        var initialOffice: IAOffice
        var isRequestInFlight: Bool
        var focus: Focus?
        @Presents var destination: Destination.State?
        var numberOfSeats: String
        var isSaveButtonDisabled: Bool

        public init(office: Shared<IAOffice>) {
            self._office = office
            self.officeTitleTextField = ValidatableTextFieldFeature.State(text: office.title, placeholder: MR.strings().seat_reservation_office_name_placeholder.desc().localized())
            self.numberOfSeats = "\(office.wrappedValue.numberOfSeats)"
            self.initialOffice = office.wrappedValue
            self.isRequestInFlight = false
            self.destination = nil
            self.focus = .officeTitle
            self.isSaveButtonDisabled = true
        }

        public enum Focus: Equatable, Sendable {
            case officeTitle
            case officeCapacity
        }
    }

    public enum Action: Sendable, ViewAction, BindableAction {
        case binding(BindingAction<State>)
        case destination(PresentationAction<Destination.Action>)
        case editOfficeResponse(Result<Void, Error>)
        case officeTitleTextField(ValidatableTextFieldFeature.Action)
        case officeTitleChanged(String)
        case view(View)

        public enum View: Sendable {
            case onTask
            case onSaveButtonTapped
            case onCancelButtonTapped
        }

        public enum Alert: Equatable, Sendable {
            case successConfirmation
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        ReducerReader { state, _ in
            Scope(state: \.officeTitleTextField, action: \.officeTitleTextField) {
                ValidatableTextFieldFeature { title in
                    validateTitle(state.office, title)
                }
            }
        }

        Reduce<State, Action> { state, action in
            switch action {
            case .view(.onTask):
                return .publisher { state.$office.title.publisher.map(Action.officeTitleChanged) }

            case .view(.onCancelButtonTapped):
                return .run { _ in
                    await dismiss()
                }

            case .view(.onSaveButtonTapped):
                state.focus = nil
                state.isRequestInFlight = true
                return .run { [office = state.office] send in
                    await send(.editOfficeResponse(Result { try await editOffice(office) }))
                }

            case .editOfficeResponse(.success):
                state.isRequestInFlight = false
                state.destination = .alert(.successfullEditingConfirmation)
                return .none

            case .editOfficeResponse(.failure):
                state.isRequestInFlight = false
                state.destination = .alert(.failedEditingConfirmation)
                return .none

            case .destination(.presented(.alert(.successConfirmation))):
                return .run { _ in
                    await dismiss()
                }

            case .destination:
                return .none

            case .binding(\.numberOfSeats):
                if let numberOfSeats = Int(state.numberOfSeats) {
                    state.office.numberOfSeats = numberOfSeats
                }
                return validateFields(state: &state)
                
            case .officeTitleChanged:
                return validateFields(state: &state)

            case .officeTitleTextField:
                return .none

            case .binding:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func validateFields(state: inout State) -> EffectOf<Self> {
        state.isSaveButtonDisabled = !validateEditing(state.initialOffice, state.office.title, state.numberOfSeats)
        return .none
    }
}

extension OfficeFormFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case alert(AlertState<OfficeFormFeature.Action.Alert>)
    }
}

extension AlertState where Action == OfficeFormFeature.Action.Alert {
    static let successfullEditingConfirmation = AlertState {
        TextState(resource: \.seat_reservation_success_edit_office_message)
    } actions: {
        ButtonState(action: .successConfirmation) {
            TextState(resource: \.general_ok)
        }
    }

    static let failedEditingConfirmation = AlertState {
        TextState(resource: \.seat_reservation_failure_edit_office_message)
    }
}
