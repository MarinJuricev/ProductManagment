package parking.myReservations.details.interaction

sealed interface MyReservationDetailsViewEffect {
    data class ShowMessage(val message: String) : MyReservationDetailsViewEffect
    data object ReservationUpdated : MyReservationDetailsViewEffect
}
