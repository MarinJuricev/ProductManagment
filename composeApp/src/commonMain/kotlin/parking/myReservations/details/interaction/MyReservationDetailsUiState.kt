package parking.myReservations.details.interaction

import components.QuestionDialogData
import parking.reservation.model.ParkingReservationStatusUiModel

sealed interface MyReservationDetailsUiState {
    data object Loading : MyReservationDetailsUiState
    data class Content(
        val id: String,
        val email: String,
        val createdAt: Long,
        val updatedAt: Long,
        val reservationStatus: ParkingReservationStatusUiModel,
        val dateInfo: DateInfo,
        val adminNotes: AdminNotes,
        val garageInfo: GarageInfo,
        val cancelButton: CancelButton,
        val cancelRequestDialog: QuestionDialogData?,
    ) : MyReservationDetailsUiState

    data object Error : MyReservationDetailsUiState
}

data class DateInfo(
    val dateFormTitle: String,
    val date: String,
    val dateTimestamp: Long,
)

data class AdminNotes(
    val notesForAdminFormTitle: String,
    val notesForAdmin: String,
    val adminNoteFormVisible: Boolean,
    val additionNotesFormPlaceholderText: String,
    val adminNoteFormTitle: String,
    val adminNote: String,
)

data class GarageInfo(
    val garageLevelVisible: Boolean,
    val garageLevel: String,
    val parkingSpotVisible: Boolean,
    val parkingSpot: String,
    val garageLevelFormTitle: String,
    val parkingSpotFormTitle: String,
)

data class CancelButton(
    val cancelButtonLoading: Boolean,
    val cancelButtonVisible: Boolean,
    val cancelButtonText: String,
)
