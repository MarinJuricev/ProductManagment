import Shared

extension IAEmailTemplate {
    public enum IAStatus: Equatable, Sendable {
        case submitted
        case approved
        case declined
        case canceled
        case canceledByAdmin

        public init(_ status: TemplateStatus) {
            switch onEnum(of: status) {
            case .approved:
                self = .approved
            case .canceled:
                self = .canceled
            case .declined:
                self = .declined
            case .submitted:
                self = .submitted
            case .canceledByAdmin:
                self = .canceledByAdmin
            }
        }

        public func toTemplateStatus() -> TemplateStatus {
            switch self {
            case .approved:
                return .Approved()
            case .canceled:
                return .Canceled()
            case .declined:
                return .Declined()
            case .submitted:
                return .Submitted()
            case .canceledByAdmin:
                return .CanceledByAdmin()
            }
        }
    }
}
