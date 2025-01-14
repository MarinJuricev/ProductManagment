import ComposableArchitecture
import LoginFeature
import MainFeature
import Foundation
import AuthenticationClient
import Core
import Shared

@Reducer
public struct AppFeature: Sendable {

    public init(environment: String?) {
        let environment = environmentFromString(value: environment) ?? .development
        InitKoinKt.doInitKoin(environment: environment)
    }

    @Dependency(\.authenticationClient.retrieveUser) var retrieveUser

    @ObservableState
    public struct State: Equatable, Sendable {
        var appMode: AppMode.State

        public init(appMode: AppMode.State = .splash) {
            self.appMode = appMode
        }

        mutating func displayLoginFeature() {
            appMode = .login(LoginFeature.State())
        }

        mutating func displayMainFeature(with user: IAUser) {
            appMode = .main(
                MainFeature.State(user: user)
            )
        }
    }

    public enum Action: ViewAction, Sendable {
        case appMode(AppMode.Action)
        case userResponse(Result<IAUser, Error>)
        case view(ViewAction)

        @CasePathable
        public enum ViewAction: Sendable {
            case onTask
        }
    }

    public var body: some ReducerOf<Self> {
        Scope(state: \.appMode, action: \.appMode) {
            AppMode.body
        }

        Reduce { state, action in
            switch action {
            case .view(let viewAction):
                switch viewAction {
                case .onTask:
                    return retrieveLoggedUser()
                }

            case .userResponse(.success(let user)):
                state.displayMainFeature(with: user)
                return .none

            case .userResponse(.failure):
                state.displayLoginFeature()
                return .none

            case .appMode(.login(.delegate(let delegateAction))):
                switch delegateAction {
                case .didLogin(let user):
                    state.displayMainFeature(with: user)
                    return .none
                }

            case .appMode(.main(.delegate(let delegateAction))):
                switch delegateAction {
                case .didLogout:
                    state.displayLoginFeature()
                    return .none
                }

            case .appMode:
                return .none
            }
        }
    }

    private func retrieveLoggedUser() -> EffectOf<Self> {
        return .run { send in
            await send(.userResponse( Result { try await retrieveUser() }))
        }
    }
}

extension AppFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum AppMode {
        case login(LoginFeature)
        case main(MainFeature)
        @ReducerCaseIgnored
        case splash
    }
}
