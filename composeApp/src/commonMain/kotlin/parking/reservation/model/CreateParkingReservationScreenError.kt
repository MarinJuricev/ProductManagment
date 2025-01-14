package parking.reservation.model

sealed class CreateParkingReservationScreenError {
    data object ProfileNotFound : CreateParkingReservationScreenError()
}
