package parking.reservation.model

import core.model.MarinJuricevError

sealed class ParkingReservationError : MarinJuricevError() {
    data object Unauthorized : ParkingReservationError()
    data object InvalidEmailFormat : ParkingReservationError()
    data object OnlyFutureDateReservationsAllowed : ParkingReservationError()
    data object DuplicateReservation : ParkingReservationError()
    data object LateReservation : ParkingReservationError()
    data class ErrorMessage(val message: String) : ParkingReservationError()
    data object UnknownError : ParkingReservationError()
}
