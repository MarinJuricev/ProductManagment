import ComposableArchitecture

@Reducer
public struct ValidatableTextFieldFeature {

    private var validate: @Sendable (String) -> String?

    public init(validate: @Sendable @escaping (String) -> String?) {
        self.validate = validate
    }

    @ObservableState
    public struct State: Equatable, Sendable {
        @Shared public var text: String
        public var placeholder: String
        public var errorMessage: String?
        var isInitialyValid: Bool

        public var isValid: Bool {
            errorMessage == nil && isInitialyValid
        }

        public init(
            text: Shared<String>,
            placeholder: String,
            errorMessage: String? = nil,
            isInitialyValid: Bool = false
        ) {
            self._text = text
            self.placeholder = placeholder
            self.errorMessage = errorMessage
            self.isInitialyValid = isInitialyValid
        }

        public mutating func reset(isInitialyValid: Bool = false) {
            self.text = ""
            self.isInitialyValid = isInitialyValid
        }
    }

    public enum Action: BindableAction, ViewAction, Sendable {
        case binding(BindingAction<State>)
        case view(View)

        public enum View: Sendable {
            case onFirstAppear
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onFirstAppear):
                if !state.text.isEmpty {
                    state.isInitialyValid = validate(state.text) == nil
                }
                return .none

            case .binding(\.text):
                state.errorMessage = validate(state.text)
                state.isInitialyValid = true
                return .none

            case .binding:
                return .none
            }
        }
    }
}
