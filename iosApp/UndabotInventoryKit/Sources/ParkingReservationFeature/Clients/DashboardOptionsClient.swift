import Shared
import DependenciesMacros
import Dependencies
import Foundation
import Utilities
import Core

@DependencyClient
public struct DashboardOptionsClient: Sendable {
    var get: @Sendable (_ for: IAUser) async throws -> [ParkingDashboardOption]
}

extension DashboardOptionsClient: DependencyKey {
    public enum DashboardItemsError: Error {
        case castingError
    }

    public static let liveValue: DashboardOptionsClient = DashboardOptionsClient { user in
        let dashboardClient: GetParkingDashboard = Di.shared.get()
        let either = try await dashboardClient.invoke(user: InventoryAppUser(user))
        guard let items = try SwiftEither(either: either).get() as? [ParkingDashboardOption] else {
            throw DashboardItemsError.castingError
        }
       return items
    }

    public static let testValue: DashboardOptionsClient = DashboardOptionsClient { _ in
        [ParkingDashboardOption.NewRequestOption(), ParkingDashboardOption.MyReservationsOption()]
    }
}

extension DependencyValues {
    public var dashboardOptionsClient: DashboardOptionsClient {
        get { self[DashboardOptionsClient.self] }
        set { self[DashboardOptionsClient.self] = newValue }
    }
}
