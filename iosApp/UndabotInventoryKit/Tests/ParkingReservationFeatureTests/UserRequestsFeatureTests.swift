import ComposableArchitecture
import XCTest
import Shared
@testable import ParkingReservationFeature
import Core

class UserRequestsFeatureTests: XCTestCase {
    @MainActor func test_itemTapped() async {
        let reservation = IAParkingReservation.parkingReservation1
        let store = TestStore(initialState: UserRequestsFeature.State(parkingReservations: ParkingReservationsFeature.State(data: .loaded(IdentifiedArray(uniqueElements: [reservation]))))) {
            UserRequestsFeature()
        } withDependencies: {
            $0.date.now = .mockNow
        }

        await store.send(\.parkingReservations.view.itemTapped, reservation)

        await store.receive(\.parkingReservations.delegate.itemTapped, reservation) {
            $0.sheetHeightReading = UserRequestsFeature.SheetEstimator.estimate(for: reservation)
            $0.destination = .details(UserRequestDetailsFeature.State(parkingReservation: .parkingReservation1))
        }
    }
}
