@preconcurrency import Shared
@preconcurrency import GoogleSignIn
import Utilities
import Dependencies
import Core

extension AuthenticationClient: DependencyKey {
    public static let liveValue: AuthenticationClient = {
        let authentication: Authentication = Di.shared.get()
        let gidSignIn = GIDSignIn.sharedInstance
        return AuthenticationClient { @MainActor viewController in
            let result = try await gidSignIn.signIn(withPresenting: viewController)
            
            guard let email = result.user.profile?.email,
                  let credentialToken = result.user.idToken?.tokenString else {
                throw AuthError.CredentialsNotReceived()
            }
            let accessToken = result.user.accessToken.tokenString

            return GoogleAuthData(email: email, credentialToken: credentialToken, accessToken: accessToken)
        } login: { credentialToken, accessToken, email in
            let either = try await authentication.signInWithCredentialToken(credentialToken: credentialToken, accessToken: accessToken, email: email)
            let user = try SwiftEither(either: either).get()
            return IAUser(user)
        } logout: {
            try? await authentication.signOut()
            gidSignIn.signOut()
        } retrieveUser: {
            let either = try await authentication.getCurrentUser()
            let user = try SwiftEither(either: either).get()
            return IAUser(user)
        }
    }()
}

extension DependencyValues {
    public var authenticationClient: AuthenticationClient {
        get { self[AuthenticationClient.self] }
        set { self[AuthenticationClient.self] = newValue }
    }
}
