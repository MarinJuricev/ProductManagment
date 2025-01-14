import Shared
import DependenciesMacros
import Dependencies
import Foundation
import Utilities
import Core

@DependencyClient
public struct CurrentUserClient: Sendable {
    public var observe: @Sendable () -> AsyncThrowingStream<IAUser, Error> = { AsyncThrowingStream { _ in } }
}

extension CurrentUserClient: DependencyKey {
    public static let liveValue: CurrentUserClient = CurrentUserClient {
        let observeCurrentUserRole: ObserveCurrentUser = Di.shared.get()
        return AsyncThrowingStream(observeCurrentUserRole.invoke()) { IAUser($0) }
    }
}

extension DependencyValues {
    public var currentUserClient: CurrentUserClient {
        get { self[CurrentUserClient.self] }
        set { self[CurrentUserClient.self] = newValue }
    }
}
