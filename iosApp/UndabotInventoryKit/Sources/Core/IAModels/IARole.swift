import Shared

extension IAUser {
    public enum IARole: Sendable, Equatable, CaseIterable, Hashable {
        case user
        case manager
        case admin

        public var id: String {
            self.hashValue.description
        }

        init(_ role: InventoryAppRole) {
            switch onEnum(of: role) {
            case .administrator:
                self = .admin
            case .manager:
                self = .manager
            case .user:
                self = .user
            }
        }

        func toInventoryAppRole() -> InventoryAppRole {
            switch self {
            case .admin:
                return .Administrator()
            case .manager:
                return .Manager()
            case .user:
                return .User()
            }
        }
    }
}
