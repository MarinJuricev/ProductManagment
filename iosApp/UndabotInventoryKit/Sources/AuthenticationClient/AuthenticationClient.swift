import DependenciesMacros
import Core
import UIKit

@DependencyClient
public struct AuthenticationClient: Sendable {
    public var signInWithGoogle: @Sendable (_ viewController: UIViewController) async throws -> GoogleAuthData
    public var login: @Sendable (_ credentialToken: String, _ accessToken: String?, _ email: String) async throws -> IAUser
    public var logout: @Sendable () async throws -> Void
    public var retrieveUser: @Sendable () async throws -> IAUser
}
