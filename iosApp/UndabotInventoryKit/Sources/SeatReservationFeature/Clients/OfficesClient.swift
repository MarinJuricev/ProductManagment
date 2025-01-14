import DependenciesMacros
import Dependencies
import Core
import Shared
import Utilities

@DependencyClient
public struct OfficesClient: Sendable {
    var get: @Sendable () async throws -> [IAOffice]
    var observe: @Sendable () -> AsyncThrowingStream<[IAOffice], Error> = { AsyncThrowingStream { _ in } }
    var delete: @Sendable (IAOffice) async throws -> Void
    var edit: @Sendable (IAOffice) async throws -> Void
    var add: @Sendable (String, String) async throws -> Void
}

extension OfficesClient: DependencyKey {
    public enum OfficesClientError: Error {
        case castingError
    }

    public static var liveValue: OfficesClient = OfficesClient {
        let getOffices: GetOffices = Di.shared.get()
        guard let items =  try await SwiftEither(either: getOffices.invoke()).get() as? [Office] else {
            throw OfficesClientError.castingError
        }
        return items.map { IAOffice($0) }
    } observe: {
        let observeOffices: ObserveOffices = Di.shared.get()
        return AsyncThrowingStream(observeOffices.invoke()) { IAOffice($0) }
    } delete: { office in
        let deleteOffice: DeleteOffice = Di.shared.get()
        let either = try await SwiftEither(either: deleteOffice.invoke(office: Office(office)))
        _ = try either.get()
    } edit: { office in
        let updateOffice: UpdateOffice = Di.shared.get()
        let either = try await SwiftEither(either: updateOffice.invoke(office: Office(office)))
        _ = try either.get()
    } add: { title, numberOfSeats in
        let createOffice: CreateOffice = Di.shared.get()
        let either = try await SwiftEither(either: createOffice.invoke(name: title, seats: numberOfSeats))
        _ = try either.get()
    }
}

extension DependencyValues {
    public var officesClient: OfficesClient {
        get { self[OfficesClient.self] }
        set { self[OfficesClient.self] = newValue }
    }
}
