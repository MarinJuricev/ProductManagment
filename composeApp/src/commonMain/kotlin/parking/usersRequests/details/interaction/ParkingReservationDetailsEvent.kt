package parking.usersRequests.details.interaction

import parking.reservation.model.ParkingReservationStatusUiModel
import parking.reservation.model.ParkingReservationUiModel
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState.Content
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi

sealed interface ParkingReservationDetailsEvent {
    data class ScreenShown(val selectedRequest: ParkingReservationUiModel) :
        ParkingReservationDetailsEvent

    data class StatusChanged(
        val status: ParkingReservationStatusUiModel,
    ) : ParkingReservationDetailsEvent

    data class AdminNoteChanged(val note: String) : ParkingReservationDetailsEvent
    data class GarageLevelChanged(val level: GarageLevelUi) : ParkingReservationDetailsEvent
    data class GarageSpotChanged(val spot: GarageSpotUi) : ParkingReservationDetailsEvent
    data class SaveButtonClick(val data: Content) : ParkingReservationDetailsEvent
    data class HasGarageAccessChanged(val hasAccess: Boolean) : ParkingReservationDetailsEvent
    data class RetryClick(val selectedRequest: ParkingReservationUiModel) :
        ParkingReservationDetailsEvent
}
