package parking.reservation.model

sealed interface RequestMode {
    data object Request : RequestMode
    data object Reservation : RequestMode
}
