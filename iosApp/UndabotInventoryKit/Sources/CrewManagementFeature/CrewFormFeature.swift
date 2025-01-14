import ComposableArchitecture
import Shared
import Core
import CommonUI

@Reducer
public struct CrewFormFeature: Sendable {

    @Dependency(\.dismiss) var dismiss
    @Dependency(\.createUserErrorMapper.map) var mapToAlertState
    private let validateEmail: @Sendable (String) -> String?
    private let save: @Sendable (IAUser) async throws -> Void

    public init(save: @Sendable @escaping (IAUser) async throws -> Void,
                validateEmail: @Sendable @escaping (String) -> String?
                ) {
        self.validateEmail = validateEmail
        self.save = save
    }

    @ObservableState
    public struct State: Equatable, Sendable {
        @Presents var destination: Destination.State?
        var emailTextField: ValidatableTextFieldFeature.State
        @Shared var user: IAUser
        var initialUser: IAUser
        var isRequestInFlight: Bool
        var successAlertMessage: String
        var isEmailDisabled: Bool
        var role: IAUser.IARole? {
            get { user.role }
            set {
                guard let newValue else { return }
                user.role = newValue
            }
        }

        var isSaveButtonDisabled: Bool {
            !emailTextField.isValid || initialUser == user
        }

        public init(
            user: IAUser,
            successAlertMessage: String,
            isEmailDisabled: Bool = false
        ) {
            let sharedUser = Shared(user)
            self.initialUser = user
            self._user = sharedUser
            self.successAlertMessage = successAlertMessage
            self.isEmailDisabled = isEmailDisabled
            self.isRequestInFlight = false
            self.destination = nil
            self.emailTextField = ValidatableTextFieldFeature.State(text: sharedUser.email, placeholder: MR.strings().crew_management_form_email_title.desc().localized())
        }

        public init(user: IAUser,
                    isRequestInFlight: Bool = false,
                    successAlertMessage: String,
                    destination: Destination.State? = nil,
                    isEmailDisabled: Bool
                ) {
            let sharedUser = Shared(user)
            self.initialUser = user
            self._user = sharedUser
            self.emailTextField = ValidatableTextFieldFeature.State(text: sharedUser.email, placeholder: MR.strings().crew_management_form_email_title.desc().localized())
            self.isRequestInFlight = isRequestInFlight
            self.successAlertMessage = successAlertMessage
            self.destination = destination
            self.isEmailDisabled = isEmailDisabled
        }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case view(ViewAction)
        case emailTextField(ValidatableTextFieldFeature.Action)
        case binding(BindingAction<State>)
        case saveUserResponse(Result<Void, Error>)
        case destination(PresentationAction<Destination.Action>)

        public enum ViewAction: Sendable {
            case onCancelButtonTapped
            case onSaveButtonTapped
        }

        public enum Alert: Equatable, Sendable {
            case successConfirmation
        }
    }

    enum CancelID: Hashable {
        case saveUser(IAUser.ID)
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Scope(state: \.emailTextField, action: \.emailTextField) {
            ValidatableTextFieldFeature(validate: validateEmail)
        }

        Reduce { state, action in
            switch action {
            case .saveUserResponse(.success):
                state.isRequestInFlight = false
                state.destination = .alert(.success(message: state.successAlertMessage))
                return .none

            case .saveUserResponse(.failure(let error)):
                state.isRequestInFlight = false
                state.destination = .alert(mapToAlertState(error))
                return .none

            case .view(.onCancelButtonTapped):
                return .run { _ in
                    await dismiss()
                }

            case .view(.onSaveButtonTapped):
                return updateUserRole(state: &state)

            case .binding:
                return .none

            case .emailTextField:
                return .none

            case .destination(.presented(.alert(.successConfirmation))):
                return .run { _ in
                    await dismiss()
                }

            case .destination:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func updateUserRole(state: inout State) -> EffectOf<Self> { 
        state.isRequestInFlight = true
        return .run { [user = state.user] send in
            await send(.saveUserResponse(Result { try await save(user) }))
        }
        .cancellable(id: CancelID.saveUser(state.user.id))
    }
}

extension CrewFormFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        @ReducerCaseEphemeral
        case alert(AlertState<CrewFormFeature.Action.Alert>)
    }
}

extension AlertState where Action == CrewFormFeature.Action.Alert {
    static func success(message: String) -> AlertState {
        return AlertState {
            TextState(message)
        } actions: {
            ButtonState(action: .successConfirmation) {
                TextState(resource: \.general_ok)
            }
        }
    }

    static var failure: AlertState = AlertState {
        TextState("Failed")
    } actions: {
        ButtonState(action: .successConfirmation) {
            TextState(resource: \.general_ok)
        }
    }

}
