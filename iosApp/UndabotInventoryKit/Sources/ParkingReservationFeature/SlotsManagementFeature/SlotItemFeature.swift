import ComposableArchitecture
import Core

@Reducer
public struct SlotItemFeature {
    @ObservableState
    public struct State: Identifiable, Equatable, Sendable {
        public var id: String {
            garageLevelData.id
        }
        var garageLevelData: IAGarageLevelData
        var isExpanded: Bool

        public init(
            garageLevelData: IAGarageLevelData,
            isExpanded: Bool = false
        ) {
            self.garageLevelData = garageLevelData
            self.isExpanded = isExpanded
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(View)
        case delegate(Delegate)

        public enum View: Sendable {
            case toggleExpansion
            case editButtonTapped
            case deleteButtonTapped
        }

        public enum Delegate: Sendable {
            case editButtonTapped
            case deleteButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(.toggleExpansion):
                state.isExpanded.toggle()
                return .none
            case .view(.deleteButtonTapped):
                return .send(.delegate(.deleteButtonTapped))
            case .view(.editButtonTapped):
                return .send(.delegate(.editButtonTapped))

            case .delegate:
                return .none
            }
        }
    }
}
