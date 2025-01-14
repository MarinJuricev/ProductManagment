package seat.management.interaction

import components.ImageType
import components.QuestionDialogData
import seat.management.components.EditOfficeDialogData
import seat.management.components.OfficeItemOption
import seatreservation.model.Office

sealed interface SeatManagementScreenState {
    data object Loading : SeatManagementScreenState
    data class Content(
        val screenTexts: SeatManagementScreenTexts,
        val offices: List<Office>,
        val officeName: String,
        val seatsNumber: String,
        val addButtonEnabled: Boolean,
        val deleteDialog: QuestionDialogData?,
        val editDialog: EditOfficeDialogData?,
        val availableOptions: List<OfficeItemOption>,
        val itemMenuIconResource: ImageType.Resource,
        val officeNamePlaceHolder: String,
        val seatsNumberPlaceholder: String,
    ) : SeatManagementScreenState
}

data class SeatManagementScreenTexts(
    val officeNameLabel: String,
    val seatsAmountLabel: String,
    val officeNamePlaceHolder: String,
)
