import Core
import Shared
import Utilities

extension IAUser.IARole {
    public var title: String {
        switch self {
        case .user:
            "User"
        case .manager:
            "Manager"
        case .admin:
            "Administrator"
        }
    }
}
