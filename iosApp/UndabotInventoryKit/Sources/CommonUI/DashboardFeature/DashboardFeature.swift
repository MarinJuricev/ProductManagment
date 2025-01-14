import ComposableArchitecture
import Core

@Reducer
public struct DashboardFeature<Option: IADashboardOption> {

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var options: IdentifiedArrayOf<Option>

        public init(options: IdentifiedArrayOf<Option>) {
            self.options = options
        }
    }

    public enum Action: Sendable {
        case optionTapped(Option)
        case delegate(Delegate)

        public enum Delegate: Sendable {
            case optionTapped(Option)
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce { _, action in
            switch action {
            case .optionTapped(let option):
                return .send(.delegate(.optionTapped(option)))
            case .delegate:
                return .none
            }
        }
    }
}
