import Dependencies
import Foundation
import DependenciesMacros
import Shared
import Utilities
import Core

@DependencyClient
public struct ParkingReservationsClient: Sendable {
    var createReservation: @Sendable (_ request: ParkingRequest) async throws -> Void
    var createRequests: @Sendable (_ requests: [ParkingRequest]) throws -> AsyncStream<[DateResponse]> = { _ in AsyncStream { _ in

    } }
    var submitUpdatedReservation: @Sendable (_ reservation: IAParkingReservation) async throws -> Void
    var observe: @Sendable (_ startDate: Int64, _ endDate: Int64) -> AsyncThrowingStream<[IAParkingReservation], Error> = { _, _ in AsyncThrowingStream { _ in } }
}

extension ParkingReservationsClient: DependencyKey {
    public enum ParkingReservationClientError: Error {
        case castingError
    }
    public static let liveValue: ParkingReservationsClient = ParkingReservationsClient { request in
        let manageParkingRequest: ManageParkingRequest = Di.shared.get()

        let either = try await manageParkingRequest.invoke(request: request)
        _ = try SwiftEither(either: either).get()
    } createRequests: { requests in
        let requestMultipleParkingPlaces: RequestMultipleParkingPlaces = Di.shared.get()
        let either = requestMultipleParkingPlaces.invoke(requests: requests)
        let skieKotlinFlow = try SwiftEither(either: either).get()
        let skieSwiftFlow: SkieSwiftFlow<NSDictionary> = SkieSwiftFlow(skieKotlinFlow)
        return AsyncStream { continuation in
            Task {
                for await NSDictionary in skieSwiftFlow {
                    guard let dictionary = NSDictionary as? [Int64: MultipleParkingRequestState] else { return }
                    let array = dictionary.map { DateResponse(date: Date(milliseconds: $0.key), state: $0.value) }
                    continuation.yield(array)
                }
            }
        }
    } submitUpdatedReservation: { parkingReservation in
        let updateUserRequest: UpdateUserRequest = Di.shared.get()

        let either = try await updateUserRequest.invoke(parkingReservation: ParkingReservation(parkingReservation))
        _ = try SwiftEither(either: either).get()
    } observe: { startDate, endDate in
        let observeMyParkingRequests: ObserveMyParkingRequests = Di.shared.get()
        return AsyncThrowingStream(observeMyParkingRequests.invoke(startDate: startDate, endDate: endDate)) { IAParkingReservation($0) }
    }
    public static let userRequestsObservation: @Sendable (_ startDate: Int64, _ endDate: Int64) -> AsyncThrowingStream<[IAParkingReservation], Error> = { startDate, endDate in
        let observeUserRequests: ObserveUserRequests = Di.shared.get()
        return AsyncThrowingStream(observeUserRequests.invoke(startDate: startDate, endDate: endDate)) { IAParkingReservation($0) }
    }
}

extension DependencyValues {
    public var parkingReservationsClient: ParkingReservationsClient {
        get { self[ParkingReservationsClient.self] }
        set { self[ParkingReservationsClient.self] = newValue }
    }
}

extension String {
    public static let mock = "1715935068"
}

extension Int64 {
    public static let mock = Int64(1715935068)
}

extension ParkingReservation {
    @MainActor
    // swiftlint:disable:next line_length
    public static let mockSubmitted = ParkingReservation(id: UUID(), email: "dummy@mail.com", date: .mock, note: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.", status: .Submitted(), createdAt: .mock, updatedAt: .mock)

    @MainActor
    // swiftlint:disable:next line_length
    public static let mockDenied = ParkingReservation(id: UUID(), email: "dummy@mail.com", date: .mock, note: "Note from the user", status: .Declined(adminNote: "Sorry, there is no parking available"), createdAt: .mock, updatedAt: .mock)

    @MainActor
    // swiftlint:disable:next line_length
    public static let mockApproved = ParkingReservation(id: UUID(), email: "dummy@mail.com", date: .mock, note: "Note from the user", status: .Approved(adminNote: "Approved bato", parkingCoordinate: ParkingCoordinate(level: GarageLevel(id: UUID(), title: "-11"), spot: ParkingSpot(id: UUID(), title: "22"))), createdAt: .mock, updatedAt: .mock)

    @MainActor
    // swiftlint:disable:next line_length
    public static let mockApprovedWithoutNote = ParkingReservation(id: UUID(), email: "dummy@mail.com", date: .mock, note: "Note from the user", status: .Approved(adminNote: "", parkingCoordinate: ParkingCoordinate(level: GarageLevel(id: UUID(), title: "-11"), spot: ParkingSpot(id: UUID(), title: "22"))), createdAt: .mock, updatedAt: .mock)

    @MainActor
    public static let mockCanceled = ParkingReservation(id: UUID(), email: "dummy@mail.com", date: .mock, note: "Note from the user", status: .Canceled(), createdAt: .mock, updatedAt: .mock)
}
