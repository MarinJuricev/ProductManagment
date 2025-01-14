import Dependencies
import Shared
import Utilities
import Foundation
import Core

struct CrewClient: Sendable {
    var observeUsers: @Sendable () -> AsyncThrowingStream<[IAUser], Error>
    var updateUser: @Sendable (IAUser) async throws -> Void
    var createUser: @Sendable (IAUser) async throws -> Void
}

extension CrewClient: DependencyKey {
    public enum UsersError: Error {
        case castingError
    }

    static let liveValue: CrewClient = CrewClient {
        let observeUsers: ObserveUsers = Di.shared.get()
        return AsyncThrowingStream(observeUsers.invoke()) { IAUser($0) }
    } updateUser: { user in
        let updateUser: UpdateUser = Di.shared.get()
        let either = try await updateUser.invoke(user: InventoryAppUser(user))
        _ = try SwiftEither(either: either).get()
    } createUser: { user in
        let createUser: CreateUser = Di.shared.get()
        let either = try await createUser.invoke(user: InventoryAppUser(user))
        _ = try SwiftEither(either: either).get()
    }
}

extension DependencyValues {
    var crewClient: CrewClient {
        get { self[CrewClient.self] }
        set { self[CrewClient.self] = newValue }
    }
}
