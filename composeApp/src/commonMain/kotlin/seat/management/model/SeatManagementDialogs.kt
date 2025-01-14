package seat.management.model

import components.QuestionDialogData
import seat.management.components.EditOfficeDialogData

data class SeatManagementDialogs(
    val deleteDialogData: QuestionDialogData? = null,
    val editOfficeDialogData: EditOfficeDialogData? = null,
)
