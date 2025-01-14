package parking.reservation.interaction

import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi
import user.model.InventoryAppUser

sealed interface CreateParkingReservationEvent {
    data class DateSelected(val timeStamp: Long) : CreateParkingReservationEvent
    data class NoteChanged(val note: String) : CreateParkingReservationEvent
    data object SubmitButtonClick : CreateParkingReservationEvent
    data class ChangeUserClick(val currentlySelectedUser: InventoryAppUser) :
        CreateParkingReservationEvent

    data class UserChanged(val user: InventoryAppUser) : CreateParkingReservationEvent
    data object RetryFetchGarageData : CreateParkingReservationEvent
    data class HasGarageAccessChanged(val hasAccess: Boolean) : CreateParkingReservationEvent
    data class GarageLevelChanged(val garageLevelUi: GarageLevelUi) : CreateParkingReservationEvent
    data class GarageSpotChanged(val garageSpotUi: GarageSpotUi) : CreateParkingReservationEvent
    data class MultiDateSelectionAdded(val selectedDate: Long) : CreateParkingReservationEvent
    data class MultiDateSelectionRemoved(val selectedDate: Long) : CreateParkingReservationEvent
}
