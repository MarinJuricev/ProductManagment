package parking.usersRequests.details.interaction

import parking.reservation.model.ParkingReservation

sealed interface ParkingReservationDetailsViewEffect {
    data class ShowMessage(val message: String) : ParkingReservationDetailsViewEffect
    data class ReservationUpdated(val reservation: ParkingReservation) : ParkingReservationDetailsViewEffect
}
