import Foundation
import Shared

public struct IAUser: Identifiable, Equatable, Sendable {
    public var id: String
    public var email: String
    public var role: IARole
    public var profileImageUrl: URL?
    public var hasPermanentGarageAccess: Bool

    public init(
        id: String,
        email: String,
        role: IARole,
        profileImageUrl: URL? = nil,
        hasPermanentGarageAccess: Bool
    ) {
        self.id = id
        self.email = email
        self.role = role
        self.profileImageUrl = profileImageUrl
        self.hasPermanentGarageAccess = hasPermanentGarageAccess

    }

    public init(_ user: InventoryAppUser) {
        self.id = user.id
        self.email = user.email
        self.role = IARole(user.role)
        self.profileImageUrl = URL(string: user.profileImageUrl)
        self.hasPermanentGarageAccess = user.hasPermanentGarageAccess
    }
}

extension InventoryAppUser {
    public convenience init(_ user: IAUser) {
        self.init(
            id: user.id,
            email: user.email,
            profileImageUrl: user.profileImageUrl?.description ?? "",
            role: user.role.toInventoryAppRole(),
            hasPermanentGarageAccess: user.hasPermanentGarageAccess
        )
    }
}
