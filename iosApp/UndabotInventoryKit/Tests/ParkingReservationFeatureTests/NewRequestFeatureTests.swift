import ComposableArchitecture
import XCTest
import Shared
@testable import ParkingReservationFeature

class NewRequestFeatureTests: XCTestCase {
    @MainActor func test_form_binding() async {
        let store = TestStore(initialState: NewRequestFeature.State(user: .mock)) {
            NewRequestFeature()
        } withDependencies: {
            $0.date.now = .mockNow
        }

        await store.send(\.binding.date, .mockFuture) {
            $0.date = .mockFuture
        }

        let dummyNote = "This is a dummy note"

        await store.send(\.binding.note, dummyNote) {
            $0.note = dummyNote
        }
    }

    @MainActor func test_form_submitFailure() async {
        let dummyAlertState = AlertState<NewRequestFeature.Action.Alert> {
             TextState("")
         }
        let store = TestStore(initialState: NewRequestFeature.State(user: .mock, note: "This is a dummy note")) {
            NewRequestFeature()
        } withDependencies: {
            $0.date.now = .mockNow
            $0.parkingReservationsClient.createReservation = { @Sendable _ in
                throw NSError(domain: "", code: 0)
            }
            $0.newRequestErrorMapper.map = { _ in
                    dummyAlertState
            }
        }

        await store.send(\.view.submitTapped) {
            $0.isRequestInFlight = true
        }
        await store.receive(\.parkingReservationResponse.failure) {
            $0.isRequestInFlight = false
            $0.destination = .alert(dummyAlertState)
        }
    }
}

extension InventoryAppUser {
    public static let mock = InventoryAppUser(id: "2", email: "dummy@mail.com", profileImageUrl: "dummyURL", role: .Administrator())
}

extension Date {
    public static let mockNow = Date(timeIntervalSince1970: 1715935068)
    public static let mockFuture = Date(timeIntervalSince1970: 1716192000)
}

extension ParkingReservation {
    public static let mock = ParkingReservation(id: "5", email: "", date: .mock, note: "", status: .Submitted(), createdAt: .mock, updatedAt: .mock)
}

extension Int64 {
    public static let mock = Int64(1715935068)
}
