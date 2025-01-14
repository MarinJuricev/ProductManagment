import ComposableArchitecture
import Utilities
import Core
@Reducer
public struct UsersPickerFeature {

    @Dependency(\.userClient.getUsers) var getUsers
    @Dependency(\.dismiss) var dismiss

    @ObservableState
    public struct State: Sendable, Equatable {
        var searchText: String
        var usersData: LoadableState<IdentifiedArrayOf<IAUser>>
        @Shared var sharedSelectedUser: IAUser
        var selectedUser: IAUser

        init(selectedUser: Shared<IAUser>) {
            self._sharedSelectedUser = selectedUser
            self.selectedUser = selectedUser.wrappedValue
            self.searchText = ""
            self.usersData = .loading
        }

        var filteredUsers: IdentifiedArrayOf<IAUser> {
            if searchText.isEmpty {
                return usersData.loaded ?? []
            } else {
                return usersData.loaded?.filter({ $0.email.username.lowercased().contains(searchText.lowercased()) }) ?? []
            }
        }
    }

    public enum Action: Sendable, ViewAction, BindableAction {
        case view(View)
        case binding(BindingAction<State>)
        case usersResponse(Result<[IAUser], Error>)

        public enum View: Sendable {
            case onTask
            case onRetryButtonTapped
            case onUserTapped(IAUser)
            case onCancelButtonTapped
            case onSaveButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return getUsers(state: &state)

            case .view(.onRetryButtonTapped):
                return getUsers(state: &state)

            case .view(.onUserTapped(let user)):
                state.selectedUser = user
                return .none

            case .view(.onSaveButtonTapped):
                state.sharedSelectedUser = state.selectedUser
                return .run { _ in
                    await dismiss()
                }

            case .view(.onCancelButtonTapped):
                return .run { _ in
                    await dismiss()
                }

            case .usersResponse(.success(let users)):
                state.usersData = .loaded(IdentifiedArray(uniqueElements: users))
                return .none

            case .usersResponse(.failure):
                state.usersData = .failed
                return .none

            case .binding:
                return .none
            }
        }
    }

    private func getUsers(state: inout State) -> EffectOf<Self> {
        state.usersData = .loading
        return .run { send in
            await send(.usersResponse(Result { try await getUsers()}))
        }
    }
}
