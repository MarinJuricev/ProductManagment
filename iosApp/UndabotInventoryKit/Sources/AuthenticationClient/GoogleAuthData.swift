public struct GoogleAuthData: Sendable {
    public let email: String
    public let credentialToken: String
    public let accessToken: String

    public init(email: String, credentialToken: String, accessToken: String) {
        self.email = email
        self.credentialToken = credentialToken
        self.accessToken = accessToken
    }
}
