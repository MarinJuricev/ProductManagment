import ComposableArchitecture

@Reducer
public struct SideMenuFeature {

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        @Shared var isPresented: Bool
        @Shared var selectedFeature: SideMenuOption
        @Shared var features: [SideMenuOption]

        init(isPresented: Shared<Bool>,
             selectedFeature: Shared<SideMenuOption>,
             features: Shared<[SideMenuOption]>) {
            self._isPresented = isPresented
            self._selectedFeature = selectedFeature
            self._features = features
        }
    }

    public enum Action: BindableAction, ViewAction, Sendable {
        case binding(BindingAction<State>)
        case view(ViewAction)

        @CasePathable
        public enum ViewAction: Sendable {
            case featureSelected(SideMenuOption)
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(let viewAction):
                switch viewAction {
                case .featureSelected(let feature):
                    if state.selectedFeature != feature {
                        state.selectedFeature = feature
                    }
                    state.isPresented = false
                    return .none
                }

            case .binding:
                return .none
            }

        }
    }
}
