import Core
import Foundation

extension AuthenticationClient {
    public static let testValue: AuthenticationClient = {
        return AuthenticationClient { _ in
            GoogleAuthData(email: "", credentialToken: "", accessToken: "")
        } login: { _, _, _ in
            .mock
        } logout: {
        } retrieveUser: {
            .mock
        }
    }()
}

extension IAUser {
    @MainActor
    public static let mock = IAUser(id: UUID().uuidString, email: "", role: .admin, profileImageUrl: nil, hasPermanentGarageAccess: false)
}
