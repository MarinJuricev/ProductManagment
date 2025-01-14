import ComposableArchitecture
import AuthenticationClient
import XCTest
import Shared
import ParkingReservationFeature
@testable import MainFeature

class MainFeatureTests: XCTestCase {
    @MainActor func test_logoutConfirmed() async {
        let store = TestStore(initialState: MainFeature.State(
            user: .mock,
            parkingReservationFeature: ParkingReservationDashboardFeature.State(
                user: .mock
            )
        )) {
            MainFeature()
        }

        await store.send(\.view.logoutTapped) {
            $0.destination = .alert(.logout)
        }

        await store.send(.destination(.presented(.alert(.logoutConfirmation)))) {
            $0.destination = nil
        }

        await store.receive(\.delegate.didLogout)
    }

    @MainActor func test_logoutCancelled() async {
        let store = TestStore(initialState: MainFeature.State(
            user: .mock,
            parkingReservationFeature: ParkingReservationDashboardFeature.State(
                user: .mock
            )
        )) {
            MainFeature()
        }

        await store.send(\.view.logoutTapped) {
            $0.destination = .alert(.logout)
        }

        await store.send(.destination(.presented(.alert(.logoutCancellation)))) {
            $0.destination = nil
        }
    }

    @MainActor func test_sideMenu_toggle() async {
        let store = TestStore(initialState: MainFeature.State(
            user: .mock,
            parkingReservationFeature: ParkingReservationDashboardFeature.State(
                user: .mock
            )
        )) {
            MainFeature()
        }

        await store.send(\.view.sideMenuButtonTapped) {
            $0.isSideMenuPresented = true
            $0.sideMenuFeature.isPresented = true
        }
    }

    @MainActor func test_sideMenu_selection() async {
        let store = TestStore(initialState: MainFeature.State(
            user: .mock,
            parkingReservationFeature: ParkingReservationDashboardFeature.State(
                user: .mock
            ),
            isSideMenuPresented: true
        )) {
            MainFeature()
        }

        await store.send(\.sideMenuFeature.view.featureSelected, .testDevices) {
            $0.sideMenuFeature.selectedFeature = .testDevices
            $0.selectedFeature = .testDevices
            $0.sideMenuFeature.isPresented = false
            $0.isSideMenuPresented = false
        }
    }
}
