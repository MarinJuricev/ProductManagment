import ComposableArchitecture
import XCTest
import Shared
@testable import ParkingReservationFeature
import Core

class MyReservationsFeatureTests: XCTestCase {
    @MainActor func test_itemTapped() async {
        let reservation = IAParkingReservation.parkingReservation1
        let store = TestStore(initialState: MyReservationsFeature.State(parkingReservations: ParkingReservationsFeature.State(data: .loaded(IdentifiedArray(uniqueElements: [reservation]))))) {
            MyReservationsFeature()
        } withDependencies: {
            $0.date.now = .mockNow
        }

        await store.send(\.parkingReservations.view.itemTapped, reservation)

        await store.receive(\.parkingReservations.delegate.itemTapped, reservation) {
            $0.sheetHeightReading = MyReservationsFeature.SheetEstimator.estimate(for: reservation)
            $0.destination = .details(MyReservationDetailsFeature.State(parkingReservation: .parkingReservation1))
        }
    }
}
