import ComposableArchitecture
import Dependencies
import Core
import AuthenticationClient
import UIKit

@Reducer
public struct LoginFeature: Sendable {

    public init() {}

    @Dependency(\.authenticationClient) var authenticationClient
    @Dependency(\.loginRequestErrorMapper.map) var mapToAlertState

    @ObservableState
    public struct State: Equatable, Sendable {
        @Presents public var destination: Destination.State?

        public init(destination: Destination.State? = nil) {
            self.destination = destination
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(ViewAction)
        case googleSignInResponse(Result<GoogleAuthData, Error>)
        case loginResponse(Result<IAUser, Error>)
        case destination(PresentationAction<Destination.Action>)
        case delegate(Delegate)

        @CasePathable
        public enum ViewAction: Sendable {
            case loginButtonTapped(UIViewController?)
        }

        @CasePathable
        public enum Delegate: Equatable, Sendable {
            case didLogin(IAUser)
        }

        public enum Alert: Equatable, Sendable {}
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(let viewAction):
                switch viewAction {
                case .loginButtonTapped(let rootViewController):
                    return signInWithGoogle(from: rootViewController)
                }

            case .loginResponse(.failure(let error)):
                state.destination = .alert(mapToAlertState(error))
                return .none

            case .loginResponse(.success(let user)):
                return .send(.delegate(.didLogin(user)))

            case .googleSignInResponse(.success(let response)):
                return login(with: response)

            case .googleSignInResponse(.failure):
                return .none

            case .destination:
                return .none

            case .delegate:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func signInWithGoogle(from rootViewController: UIViewController?) -> EffectOf<Self> {
        guard let rootViewController else { return .none }
        return .run { send in
            await send(.googleSignInResponse(Result { try await authenticationClient.signInWithGoogle(rootViewController) }))
        }
    }

    private func login(with googleAuthData: GoogleAuthData) -> EffectOf<Self> {
        return .run { send in
            await send(
                .loginResponse(Result {
                    try await authenticationClient.login(
                        credentialToken: googleAuthData.credentialToken,
                        accessToken: googleAuthData.accessToken,
                        email: googleAuthData.email
                    )
                })
            )
        }
    }
}

extension LoginFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        @ReducerCaseEphemeral
        case alert(AlertState<LoginFeature.Action.Alert>)
    }
}
