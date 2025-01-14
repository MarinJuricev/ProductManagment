import Dependencies
import Utilities
import DependenciesMacros
import Foundation
import Shared
import Core

@DependencyClient
public struct GarageLevelsClient: Sendable {
    var getEmptyLevels: @Sendable (_ for: Date, _ id: String?) async throws -> [IAGarageLevelData]
    var deleteLevel: @Sendable (_ with: String) async throws -> Void
    var observeLevels: @Sendable () -> AsyncThrowingStream<[IAGarageLevelData], Error> = { AsyncThrowingStream { _ in } }
}

extension GarageLevelsClient: DependencyKey {
    public enum GarageLevelsClientError: Error {
        case castingError
    }

    public static let liveValue: GarageLevelsClient = GarageLevelsClient { date, id in
        let getEmptyParkingSpots: GetEmptyParkingSpots = Di.shared.get()
        let either = try await getEmptyParkingSpots.invoke(date: date.millisecondsSince1970, userRequestId: id)
        guard let items = try SwiftEither(either: either).get() as? [GarageLevelData] else {
            throw GarageLevelsClientError.castingError
        }
        return items.map { IAGarageLevelData($0) }
    } deleteLevel: { id in
        let deleteGarageLevel: DeleteGarageLevel = Di.shared.get()
        let either = try await deleteGarageLevel.invoke(id: id)
        _ = try SwiftEither(either: either).get()
    } observeLevels: {
        let observeGarageLevels: ObserveGarageLevelsData = Di.shared.get()
        return AsyncThrowingStream(observeGarageLevels.invoke()) { IAGarageLevelData($0) }
    }
}

extension DependencyValues {
    public var garageLevelsClient: GarageLevelsClient {
        get { self[GarageLevelsClient.self] }
        set { self[GarageLevelsClient.self] = newValue }
    }
}
