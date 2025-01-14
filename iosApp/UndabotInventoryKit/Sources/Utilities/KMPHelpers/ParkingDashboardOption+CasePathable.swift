import CasePaths
import Shared

extension ParkingDashboardOption: @retroactive CasePathable {
    public struct AllCasePaths {
        public var newRequest: AnyCasePath<ParkingDashboardOption, NewRequestOption> {
            AnyCasePath(
                embed: { _ in
                    NewRequestOption()
                },
                extract: {
                    guard case .newRequestOption = onEnum(of: $0) else { return nil }
                    return NewRequestOption()
                }
            )
        }

        public var userRequests: AnyCasePath<ParkingDashboardOption, UserRequestsOption> {
            AnyCasePath(
                embed: { _ in
                    UserRequestsOption()
                },
                extract: {
                    guard case .userRequestsOption = onEnum(of: $0) else { return nil }
                    return UserRequestsOption()
                }
            )
        }

        public var myReservations: AnyCasePath<ParkingDashboardOption, MyReservationsOption> {
            AnyCasePath(
                embed: { _ in
                        .MyReservationsOption()
                },
                extract: {
                    guard case .myReservationsOption = onEnum(of: $0) else { return nil }
                    return .MyReservationsOption()
                }
            )
        }

        public var emailTemplates: AnyCasePath<ParkingDashboardOption, EmailTemplatesOption> {
            AnyCasePath(
                embed: { _ in
                        .EmailTemplatesOption()
                },
                extract: {
                    guard case .emailTemplatesOption = onEnum(of: $0) else { return nil }
                    return .EmailTemplatesOption()
                }
            )
        }

        public var slotsManagement: AnyCasePath<ParkingDashboardOption, SlotsManagementOption> {
            AnyCasePath(
                embed: { _ in
                        .SlotsManagementOption()
                },
                extract: {
                    guard case .slotsManagementOption = onEnum(of: $0) else { return nil }
                    return .SlotsManagementOption()
                }
            )
        }
    }

    public static var allCasePaths: AllCasePaths { AllCasePaths() }
}
