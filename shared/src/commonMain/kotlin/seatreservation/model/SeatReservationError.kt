package seatreservation.model

import core.model.MarinJuricevError

sealed class SeatReservationError : MarinJuricevError() {
    data object InvalidDates : SeatReservationError()
    data object OfficeNotFound : SeatReservationError()
    data object OfficeAlreadyFull : SeatReservationError()
    data object SeatReservationFetchFailed : SeatReservationError()
    data object CurrentUserNotFound : SeatReservationError()
    data object CancelationFailed : SeatReservationError()
    data object Unauthorized : SeatReservationError()
    data object ReservationNotFound : SeatReservationError()
}
