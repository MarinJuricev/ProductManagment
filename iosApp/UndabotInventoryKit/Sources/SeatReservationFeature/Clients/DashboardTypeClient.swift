import Dependencies
import DependenciesMacros
import Core
import Shared
import Utilities

@DependencyClient
public struct DashboardTypeClient {
    var get: (_ for: IAUser) async throws -> IASeatReservationDashboardType
}

extension DashboardTypeClient: DependencyKey {
    static public var liveValue: DashboardTypeClient = DashboardTypeClient { user in
        let getDashboard: GetSeatReservationDashboardType = Di.shared.get()
        let seatReservationDashboardType = try await SwiftEither(either: getDashboard.invoke(user: InventoryAppUser(user))).get()
        return IASeatReservationDashboardType(seatReservationDashboardType)
    }
}

extension DependencyValues {
    public var seatReservationDashboardTypeClient: DashboardTypeClient {
        get { self[DashboardTypeClient.self] }
        set { self[DashboardTypeClient.self] = newValue }
    }
}
