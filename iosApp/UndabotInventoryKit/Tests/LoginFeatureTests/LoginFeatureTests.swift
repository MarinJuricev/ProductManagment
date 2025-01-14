import ComposableArchitecture
import AuthenticationClient
import XCTest
import LoginFeature
import Shared

class LoginFeatureTests: XCTestCase {
    @MainActor func test_login_success() async {
        let store = TestStore(initialState: LoginFeature.State()) {
            LoginFeature()
        }

        let fakeViewController = UIViewController()

        await store.send(\.view.loginButtonTapped, fakeViewController)

        await store.receive(\.googleSignInResponse.success)
        await store.receive(\.loginResponse.success)
        await store.receive(\.delegate.didLogin)
    }

    @MainActor func test_login_googleSignInFailed() async {
        let store = TestStore(initialState: LoginFeature.State()) {
            LoginFeature()
        } withDependencies: {
            $0.authenticationClient.signInWithGoogle = { @Sendable _ in
                throw self.anyError
            }
        }

        let fakeViewController = UIViewController()

        await store.send(\.view.loginButtonTapped, fakeViewController)

        await store.receive(\.googleSignInResponse.failure)
    }

    @MainActor func test_login_authenticationFailed() async {
        let dummyAlertState = AlertState<LoginFeature.Action.Alert> {
            TextState("")
        }
        let store = TestStore(initialState: LoginFeature.State()) {
            LoginFeature()
        } withDependencies: {
            $0.authenticationClient.login = { @Sendable _, _, _ in
                throw NSError(domain: "", code: 0)
            }
            $0.loginRequestErrorMapper.map = { _ in
                dummyAlertState
            }
        }

        let fakeViewController = UIViewController()

        await store.send(\.view.loginButtonTapped, fakeViewController)

        await store.receive(\.googleSignInResponse.success)
        await store.receive(\.loginResponse.failure) {
            $0.destination = .alert(dummyAlertState)
        }
    }
}

extension LoginFeatureTests {
    var anyError: Error {
        NSError(domain: "any", code: 0)
    }
}
