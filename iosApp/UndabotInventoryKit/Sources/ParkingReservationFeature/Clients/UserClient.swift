import Dependencies
import Shared
import Utilities
import Foundation
import Core

struct UserClient: Sendable {
    var getUser: @Sendable (String) async throws -> IAUser
    var getUsers: @Sendable () async throws -> [IAUser]
    var isUserAbleToMakeReservation: @Sendable (IAUser) -> Bool
}

extension UserClient: DependencyKey {
    public enum UsersError: Error {
        case castingError
    }

    static let liveValue: UserClient = UserClient { email in
        let getUser: GetExistingUser = Di.shared.get()
        let either = try await getUser.invoke(email: email)
        let user = try SwiftEither(either: either).get()
        return IAUser(user)
    } getUsers: {
        let getUsers: GetUsers = Di.shared.get()
        let either = try await getUsers.invoke()
        guard let items = try SwiftEither(either: either).get() as? [InventoryAppUser] else {
            throw UsersError.castingError
        }
        return items.map { IAUser($0) }
    } isUserAbleToMakeReservation: { user in
        let isUserAbleToMakeReservation: IsUserAbleToMakeReservation = Di.shared.get()
        return isUserAbleToMakeReservation.invoke(user: InventoryAppUser(user))

    }
}

extension DependencyValues {
    var userClient: UserClient {
        get { self[UserClient.self] }
        set { self[UserClient.self] = newValue }
    }
}
