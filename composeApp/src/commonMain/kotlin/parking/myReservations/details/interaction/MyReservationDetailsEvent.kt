package parking.myReservations.details.interaction

import parking.myReservations.details.interaction.MyReservationDetailsUiState.Content
import parking.reservation.model.ParkingReservationUiModel

sealed interface MyReservationDetailsEvent {
    data class ScreenShown(val selectedRequest: ParkingReservationUiModel) :
        MyReservationDetailsEvent

    data class CancelReservationButtonClick(val data: Content) : MyReservationDetailsEvent
    data class CancelReservationConfirmed(val data: Content) : MyReservationDetailsEvent
    data object CancelReservationDismissed : MyReservationDetailsEvent
}
