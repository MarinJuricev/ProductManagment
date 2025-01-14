import ComposableArchitecture
import Shared
import Utilities
import Core

@Reducer
public struct CrewManagementFeature: Sendable {

    public init() {}

    @Dependency(\.crewClient) var crewClient
    @Dependency(\.uuid) var uuid

    @ObservableState
    public struct State: Equatable, Sendable {
        var crewData: LoadableState<IdentifiedArrayOf<IAUser>>
        @Presents var destination: Destination.State?

        public init(
            crewData: LoadableState<IdentifiedArrayOf<IAUser>> = .loading,
            destination: Destination.State? = nil
        ) {
            self.crewData = crewData
            self.destination = destination
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(ViewAction)
        case destination(PresentationAction<Destination.Action>)
        case crewResponse(Result<[IAUser], Error>)

        public enum ViewAction: Sendable {
            case onTask
            case retryButtonTapped
            case onUserTapped(IAUser)
            case onCreateUserTapped
        }

        public enum Alert: Equatable, Sendable {}
    }

    enum CancelID: Hashable {
        case getCrew
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return fetchCrew(state: &state)

            case .view(.retryButtonTapped):
                return fetchCrew(state: &state)

            case .view(.onCreateUserTapped):
                let newUser = IAUser(id: uuid().uuidString, email: "", role: .user, hasPermanentGarageAccess: false)
                state.destination = .createForm(CrewFormFeature.State(user: newUser, successAlertMessage: MR.strings().crew_management_form_add_success.desc().localized()))
                return .none

            case .view(.onUserTapped(let user)):
                state.destination = .editForm(CrewFormFeature.State(user: user, successAlertMessage: MR.strings().crew_management_form_add_edit.desc().localized(), isEmailDisabled: true))
                return .none

            case .crewResponse(.success(let crew)):
                state.crewData = .loaded(IdentifiedArray(uniqueElements: crew))
                return .none

            case .crewResponse(.failure):
                state.crewData = .failed
                return .none

            case .destination:
                return .none

            }
        }
        .ifLet(\.$destination, action: \.destination) {
            Destination()
        }
    }

    private func fetchCrew(state: inout State) -> EffectOf<Self> {
        state.crewData = .loading
        return .run { send in
            for try await crew in crewClient.observeUsers() {
                await send(.crewResponse(.success(crew)))
            }
        } catch: { error, send in
            await send(.crewResponse(.failure(error)))
        }.cancellable(id: CancelID.getCrew, cancelInFlight: true)
    }
}

extension CrewManagementFeature {

    @Reducer
    public struct Destination {
        public enum State: Equatable, Sendable {
            case alert(AlertState<CrewManagementFeature.Action.Alert>)
            case createForm(CrewFormFeature.State)
            case editForm(CrewFormFeature.State)
        }

        public enum Action: Sendable {
            case alert(CrewManagementFeature.Action.Alert)
            case createForm(CrewFormFeature.Action)
            case editForm(CrewFormFeature.Action)
        }

        public var body: some ReducerOf<Self> {
            Scope(state: \.createForm, action: \.createForm) {
                @Dependency(\.crewClient.createUser) var createUser
                let emailValidator: IsEmailValid = Di.shared.get()
                CrewFormFeature(save: createUser) { email in
                    return emailValidator.invoke(email: email) ? nil : MR.strings().crew_management_form_invalid_email.desc().localized()
                }
            }

            Scope(state: \.editForm, action: \.editForm) {
                @Dependency(\.crewClient.updateUser) var updateUser
                let emailValidator: IsEmailValid = Di.shared.get()
                CrewFormFeature(save: updateUser) { email in
                    return emailValidator.invoke(email: email) ? nil : MR.strings().crew_management_form_invalid_email.desc().localized()
                }
            }
        }

    }
}
