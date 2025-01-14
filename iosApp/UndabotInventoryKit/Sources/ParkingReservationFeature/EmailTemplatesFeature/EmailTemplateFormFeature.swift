import ComposableArchitecture
import Core
import Utilities
import Shared

@Reducer
public struct EmailTemplateFormFeature {

    @Dependency(\.dismiss) var dismiss
    @Dependency(\.emailTemplatesClient.update) var update

    @ObservableState
    public struct State: Equatable, Sendable {
        var template: IAEmailTemplate
        var initialText: String
        @Presents var destination: Destination.State?
        var isRequestInFlight: Bool

        var isSaveButtonDisabled: Bool {
            template.text == initialText || isRequestInFlight
        }

        public init(template: IAEmailTemplate) {
            self.template = template
            self.initialText = template.text
            self.destination = nil
            self.isRequestInFlight = false
        }
    }

    public enum Action: Sendable, ViewAction, BindableAction {
        case view(View)
        case delegate(Delegate)
        case binding(BindingAction<State>)
        case destination(PresentationAction<Destination.Action>)
        case updateTemplateResponse(Result<Void, Error>)

        public enum Alert: Sendable, Equatable {
            case successConfirmation
        }

        public enum View: Sendable {
            case onSaveButtonTapped
        }

        public enum Delegate: Sendable {
            case onTemplateUpdated(IAEmailTemplate)
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onSaveButtonTapped):
                state.isRequestInFlight = true
                return .run { [template = state.template] send in
                    await send(.updateTemplateResponse(Result { try await update(template) }))
                }

            case .updateTemplateResponse(.success):
                state.isRequestInFlight = true
                state.destination = .alert(.success)
                return .none

            case .updateTemplateResponse(.failure):
                state.isRequestInFlight = true
                state.destination = .alert(.failure)
                return .none

            case .binding:
                return .none

            case .destination(.presented(.alert(.successConfirmation))):
                return .send(.delegate(.onTemplateUpdated(state.template)))

            case .destination:
                return .none

            case .delegate:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }
}

extension EmailTemplateFormFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case alert(AlertState<EmailTemplateFormFeature.Action.Alert>)
    }
}

extension AlertState where Action == EmailTemplateFormFeature.Action.Alert {
    static let success = AlertState {
        TextState("You successfully edited email template")
    } actions: {
        ButtonState(action: .successConfirmation) {
            TextState(resource: \.general_ok)
        }
    }

    static let failure = AlertState {
        TextState("Something went wrong")
    }
}
