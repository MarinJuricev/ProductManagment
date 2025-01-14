import ComposableArchitecture
import XCTest
import Shared
@testable import ParkingReservationFeature
import Core

class MyReservationDetailsFeatureTests: XCTestCase {
    @MainActor func test_cancel_failure() async {
        let reservation = IAParkingReservation.parkingReservation1
        let store = TestStore(initialState: MyReservationDetailsFeature.State(parkingReservation: reservation)) {
            MyReservationDetailsFeature()
        } withDependencies: {
            $0.cancelRequestClient.cancel = { @Sendable _ in
                throw NSError(domain: "", code: 0)
            }
            $0.cancelMyReservationErrorMapper.map = { _ in
                    .dummyAlert
            }
        }

        await store.send(\.view.cancelButtonTapped) {
            $0.isRequestInFlight = true
        }

        await store.receive(\.cancelRequestResponse.failure) {
            $0.isRequestInFlight = false
            $0.destination = .alert(.dummyAlert)
        }
    }
}

private extension AlertState where Action == MyReservationDetailsFeature.Action.Alert {
    static let dummyAlert = AlertState<MyReservationDetailsFeature.Action.Alert> {
        TextState("")
    }
}
