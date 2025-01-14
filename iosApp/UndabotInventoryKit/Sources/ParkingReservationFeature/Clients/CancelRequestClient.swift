import Dependencies
import DependenciesMacros
import Shared
import Utilities
import Core

@DependencyClient
public struct CancelRequestClient: Sendable {
    var cancel: @Sendable (_ for: IAParkingReservation) async throws -> Void
}

extension CancelRequestClient: DependencyKey {
    public static let liveValue: CancelRequestClient = CancelRequestClient { parkingReservation in
        let cancelRequest: CancelParkingPlaceRequestByUser = Di.shared.get()
        let either = try await cancelRequest.invoke(parkingReservation: ParkingReservation(parkingReservation))
        _ = try SwiftEither(either: either).get()
    }
}

extension DependencyValues {
    public var cancelRequestClient: CancelRequestClient {
        get { self[CancelRequestClient.self] }
        set { self[CancelRequestClient.self] = newValue }
    }
}
