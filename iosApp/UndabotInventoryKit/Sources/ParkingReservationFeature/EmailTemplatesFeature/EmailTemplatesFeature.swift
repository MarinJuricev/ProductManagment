import ComposableArchitecture
import Utilities
import Shared
import Dependencies
import Core

@Reducer
public struct EmailTemplatesFeature {

    @Dependency(\.emailTemplatesClient.get) var getTemplates

    @ObservableState
    public struct State: Equatable, Sendable {
        var data: LoadableState<IdentifiedArrayOf<IAEmailTemplate>>
        @Presents var destination: Destination.State?

        public init(
            data: LoadableState<IdentifiedArrayOf<IAEmailTemplate>> = .initial,
            destination: Destination.State? = nil
        ) {
            self.data = data
            self.destination = destination
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(View)
        case templatesResponse(Result<[IAEmailTemplate], Error>)
        case destination(PresentationAction<Destination.Action>)

        public enum View: Sendable {
            case onTask
            case retryButtonTapped
            case onTemplateTapped(IAEmailTemplate)
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(.onTask), .view(.retryButtonTapped):
                return fetchTemplates(state: &state)

            case .view(.onTemplateTapped(let template)):
                state.destination = .form(EmailTemplateFormFeature.State(template: template))
                return .none

            case .templatesResponse(.success(let templates)):
                state.data = .loaded(IdentifiedArray(uniqueElements: templates))
                return .none

            case .templatesResponse(.failure):
                state.data = .failed
                return .none

            case .destination(.presented(.form(.delegate(.onTemplateUpdated(let updatedTemplate))))):
                state.data.modify(\.loaded) { $0[id: updatedTemplate.id] = updatedTemplate }
                state.destination = nil
                return .none
            case .destination:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }

    func fetchTemplates(state: inout State) -> EffectOf<Self> {
        state.data = .loading
        return .run { send in
            await send(.templatesResponse(Result { try await getTemplates() }))
        }
    }
}

extension EmailTemplatesFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case form(EmailTemplateFormFeature)
    }
}
