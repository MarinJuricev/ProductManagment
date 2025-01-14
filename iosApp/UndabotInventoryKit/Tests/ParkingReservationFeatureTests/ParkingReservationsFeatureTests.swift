import ComposableArchitecture
import XCTest
import Shared
@testable import ParkingReservationFeature
import Core

class ParkingReservationsFeatureTests: XCTestCase {
    @MainActor func test_onTask_success() async {
        let store = TestStore(initialState: ParkingReservationsFeature.State()) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.parkingReservationsClient.observe = { @Sendable _, _ in
                AsyncThrowingStream { continuation in
                    continuation.yield([.parkingReservation1])
                    continuation.finish()
                }
            }
            $0.date.now = .mockNow
        }

        await store.send(\.view.onTask) {
            $0.data = .loading
        }

        await store.receive(\.parkingReservationResponse.success) {
            $0.data = .loaded(IdentifiedArray(uniqueElements: [.parkingReservation1]))
        }
    }

    @MainActor func test_onTask_failure() async {

        let store = TestStore(initialState: ParkingReservationsFeature.State()) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.parkingReservationsClient.observe = { @Sendable _, _ in
                AsyncThrowingStream { continuation in
                    continuation.finish(throwing: NSError.anyError)
                }
            }
            $0.date.now = .mockNow
        }

        await store.send(\.view.onTask) {
            $0.data = .loading
        }

        await store.receive(\.parkingReservationResponse.failure) {
            $0.data = .failed
        }
    }

    @MainActor func test_retry_success() async {
        let store = TestStore(initialState: ParkingReservationsFeature.State(data: .failed)) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.parkingReservationsClient.observe = { @Sendable _, _ in
                AsyncThrowingStream { continuation in
                    continuation.yield([.parkingReservation1])
                    continuation.finish()
                }
            }
            $0.date.now = .mockNow
        }

        await store.send(\.view.retryButtonTapped) {
            $0.data = .loading
        }

        await store.receive(\.parkingReservationResponse.success) {
            $0.data = .loaded(IdentifiedArray(uniqueElements: [.parkingReservation1]))
        }
    }

    @MainActor func test_retry_failure() async {
        let store = TestStore(initialState: ParkingReservationsFeature.State(data: .failed)) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.parkingReservationsClient.observe = { @Sendable _, _ in
                AsyncThrowingStream { continuation in
                    continuation.finish(throwing: NSError.anyError)
                }
            }
            $0.date.now = .mockNow
        }

        await store.send(\.view.retryButtonTapped) {
            $0.data = .loading
        }

        await store.receive(\.parkingReservationResponse.failure) {
            $0.data = .failed
        }
    }

    @MainActor func test_binding_dates() async {
        let store = TestStore(initialState: ParkingReservationsFeature.State(data: .loaded(IdentifiedArray(uniqueElements: [.parkingReservation1])))) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.parkingReservationsClient.observe = { @Sendable startDate, _ in
                AsyncThrowingStream { continuation in
                    if startDate == Date.mockNow.millisecondsSince1970 {
                        continuation.yield([.parkingReservation1])
                    } else {
                        continuation.yield([.parkingReservation2])
                    }
                    continuation.finish()
                }
            }
            $0.date.now = .mockNow
        }

        await store.send(\.binding.startDate, Date.mockFuture) {
            $0.data = .loading
            $0.startDate = .mockFuture
        }

        await store.receive(\.parkingReservationResponse.success) {
            $0.data = .loaded(IdentifiedArray(uniqueElements: [.parkingReservation2]))
        }

        await store.send(\.binding.startDate, Date.mockNow) {
            $0.data = .loading
            $0.startDate = .mockNow
        }

        await store.receive(\.parkingReservationResponse.success) {
            $0.data = .loaded(IdentifiedArray(uniqueElements: [.parkingReservation1]))
        }
    }

    @MainActor func test_datePickers_display() async {
        let store = TestStore(initialState: ParkingReservationsFeature.State(data: .loaded(IdentifiedArray(uniqueElements: [])))) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.date.now = .mockNow
        }

        await store.send(\.view.fromDatePickerTapped) {
            $0.visibleDatePicker = .from
        }

        await store.send(\.view.toDatePickerTapped) {
            $0.visibleDatePicker = .to
        }

        await store.send(\.view.toDatePickerTapped) {
            $0.visibleDatePicker = nil
        }

        await store.send(\.view.toDatePickerTapped) {
            $0.visibleDatePicker = .to
        }

        await store.send(\.view.fromDatePickerTapped) {
            $0.visibleDatePicker = .from
        }

        await store.send(\.view.fromDatePickerTapped) {
            $0.visibleDatePicker = nil
        }
    }

    @MainActor func test_itemTapped() async {
        let store = TestStore(initialState: ParkingReservationsFeature.State(data: .loaded(IdentifiedArray(uniqueElements: [.parkingReservation1])))) {
            ParkingReservationsFeature()
        } withDependencies: {
            $0.date.now = .mockNow
        }

        await store.send(\.view.itemTapped, .parkingReservation1)

        await store.receive(\.delegate.itemTapped, .parkingReservation1)
    }
}

extension NSError {
    static let anyError = NSError(domain: "any", code: 0)
}

extension IAParkingReservation {
    static let parkingReservation1 = IAParkingReservation(id: "1", email: "dummy@mail.com", date: .mockFuture, note: "Dummy", status: .submitted, createdAt: .now, updatedAt: .now)

    static let parkingReservation2 = IAParkingReservation(id: "2", email: "dummy@mail.com", date: .mockFuture, note: "Dummy", status: .submitted, createdAt: .now, updatedAt: .now)
}
