package parking.myReservations.interaction

sealed interface MyReservationsViewEffect {
    data object ReservationUpdated : MyReservationsViewEffect
}
