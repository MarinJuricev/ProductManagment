package parking.reservation.interaction

import parking.reservation.model.MultiDateSelectionState
import parking.reservation.model.RequestMode
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi
import user.model.InventoryAppUser

sealed interface CreateParkingReservationScreenState {
    data class Content(
        val requestMode: RequestMode,
        val headerData: HeaderData,
        val datePickerData: DatePickerData,
        val notesFormData: NotesFormData,
        val garageData: GarageDataUI,
        val submitButtonData: SubmitButtonData,
        val multiDatePickerState: MultiDateSelectionState,
    ) : CreateParkingReservationScreenState

    data object Loading : CreateParkingReservationScreenState
}

data class HeaderData(
    val user: InventoryAppUser,
    val headerTitle: String,
    val selectAnotherUserEnabled: Boolean,
)

data class DatePickerData(
    val datePickerFormTitle: String,
    val datePickerConfirmButtonText: String,
    val displayedDate: String,
    val selectedTimestamp: Long,
    val lowerDateLimit: Long,
)

data class NotesFormData(
    val notesFormTitle: String,
    val notesPlaceholderText: String,
    val notes: String,
)

sealed interface GarageDataUI {
    data class Content(
        val garageLevelPickerTitle: String,
        val garageSpotPickerTitle: String,
        val noFreeSpaceMessage: String,
        val availableGarageLevels: List<GarageLevelUi>,
        val availableGarageSpots: List<GarageSpotUi>,
        val currentGarageLevel: GarageLevelUi,
        val currentGarageSpot: GarageSpotUi,
        val noSpaceLeftErrorVisible: Boolean,
        val garageAccessSwitchVisible: Boolean,
        val switchFormTitle: String,
        val hasGarageAccess: Boolean,
    ) : GarageDataUI

    data object Retry : GarageDataUI
    data object Loading : GarageDataUI
}

data class SubmitButtonData(
    val buttonEnabled: Boolean,
    val submitButtonText: String,
    val submitButtonLoading: Boolean,
)
