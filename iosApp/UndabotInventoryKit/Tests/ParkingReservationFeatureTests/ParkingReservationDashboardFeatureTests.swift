import ComposableArchitecture
import XCTest
import Shared
@testable import ParkingReservationFeature
import Core

// swiftlint:disable:next line_length
// NOTE: since we still did not resolve the issue with Moko resources in the tests, and all of our options contain moko resources, all the tests that init ParkingDashboardOption will result in crash...
final class ParkingReservationDashboardFeatureTests: XCTestCase {
    @MainActor func test_onTask_success() async {
        let store = TestStore(initialState: ParkingReservationDashboardFeature.State(user: .mock)) {
            ParkingReservationDashboardFeature()
        } withDependencies: {
            $0.dashboardOptionsClient.get = { @Sendable in
                []
            }
        }

        await store.send(\.view.onTask) {
            $0.data = .loading
        }

        await store.receive(\.dashboardItemsResponse.success) {
            $0.data = .loaded(IdentifiedArray(uniqueElements: []))
        }
    }

    @MainActor func test_onTask_failed() async {
        let store = TestStore(initialState: ParkingReservationDashboardFeature.State(user: .mock)) {
            ParkingReservationDashboardFeature()
        } withDependencies: {
            $0.dashboardOptionsClient.get = {
                throw NSError(domain: "", code: 0)
            }
        }

        await store.send(\.view.onTask) {
            $0.data = .loading
        }

        await store.receive(\.dashboardItemsResponse.failure) {
            $0.data = .failed
        }
    }

    @MainActor func test_retry_success() async {
        let store = TestStore(initialState: ParkingReservationDashboardFeature.State(user: .mock)) {
            ParkingReservationDashboardFeature()
        } withDependencies: {
            $0.dashboardOptionsClient.get = {
                []
            }
        }

        await store.send(\.view.retryButtonTapped) {
            $0.data = .loading
        }

        await store.receive(\.dashboardItemsResponse.success) {
            $0.data = .loaded(IdentifiedArray(uniqueElements: []))
        }
    }

    @MainActor func test_retry_failed() async {
        let store = TestStore(initialState: ParkingReservationDashboardFeature.State(user: .mock)) {
            ParkingReservationDashboardFeature()
        } withDependencies: {
            $0.dashboardOptionsClient.get = {
                throw NSError(domain: "", code: 0)
            }
        }

        await store.send(\.view.retryButtonTapped) {
            $0.data = .loading
        }

        await store.receive(\.dashboardItemsResponse.failure) {
            $0.data = .failed
        }
    }
}

extension IAUser {
    @MainActor
    public static let mock = IAUser(id: UUID().uuidString, email: "", role: .admin, profileImageUrl: nil)
}
