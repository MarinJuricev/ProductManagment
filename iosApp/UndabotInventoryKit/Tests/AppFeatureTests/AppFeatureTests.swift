import ComposableArchitecture
import AuthenticationClient
import XCTest
import LoginFeature
import MainFeature
import Shared
import ParkingReservationFeature
@testable import AppFeature

class AppFeatureTests: XCTestCase {
    @MainActor func test_appOpen_userRetrievingSuccess() async {
        let store = TestStore(initialState: AppFeature.State()) {
            AppFeature()
        }

        await store.send(\.view.onTask)
        await store.receive(\.userResponse.success) {
            $0.appMode = .main(
                MainFeature.State(
                    user: .mock,
                    parkingReservationFeature: ParkingReservationDashboardFeature.State(
                        user: .mock
                    )
                )
            )
        }
    }

    @MainActor func test_appOpen_userRetrievingFailed() async {
        let store = TestStore(initialState: AppFeature.State()) {
            AppFeature()
        } withDependencies: {
            $0.authenticationClient.retrieveUser = {
                throw NSError(domain: "", code: 0)
            }
        }

        await store.send(\.view.onTask)
        await store.receive(\.userResponse.failure) {
            $0.appMode = .login(LoginFeature.State())
        }
    }

    @MainActor func test_login_success() async {
        let store = TestStore(initialState: AppFeature.State(appMode: .login(LoginFeature.State()))) {
            AppFeature()
        }

        await store.send(\.appMode.login.delegate.didLogin, .mock) {
            $0.appMode = .main(
                MainFeature.State(
                    user: .mock,
                    parkingReservationFeature: ParkingReservationDashboardFeature.State(
                        user: .mock
                    )
                )
            )
        }
    }

    @MainActor func test_logout() async {
        let store = TestStore(initialState: AppFeature.State(appMode: .main(
            MainFeature.State(
                user: .mock,
                parkingReservationFeature: ParkingReservationDashboardFeature.State(
                    user: .mock
                )
            )
        ))) {
            AppFeature()
        }

        await store.send(\.appMode.main.delegate.didLogout) {
            $0.appMode = .login(LoginFeature.State())
        }
    }
}
