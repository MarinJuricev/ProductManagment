package parking.myReservations.details.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.myReservations.details.interaction.AdminNotes
import parking.myReservations.details.interaction.CancelButton
import parking.myReservations.details.interaction.DateInfo
import parking.myReservations.details.interaction.GarageInfo
import parking.myReservations.details.interaction.MyReservationDetailsUiState.Content
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.reservation.model.ParkingReservationStatus.Canceled
import parking.reservation.model.ParkingReservationStatus.Declined
import parking.reservation.model.ParkingReservationStatus.Submitted
import parking.reservation.model.ParkingReservationUiModel
import utils.isTodayOrAfter

class MyReservationDetailsUiMapper(
    private val dictionary: Dictionary,
) {
    fun map(selectedReservation: ParkingReservationUiModel) = Content(
        id = selectedReservation.id,
        email = selectedReservation.email,
        createdAt = selectedReservation.createdAtTimeStamp,
        updatedAt = selectedReservation.updatedAtTimeStamp,
        reservationStatus = selectedReservation.reservationStatus,
        dateInfo = DateInfo(
            dateFormTitle = dictionary.getString(MR.strings.parking_reservation_item_date),
            date = selectedReservation.date,
            dateTimestamp = selectedReservation.dateTimeStamp,
        ),

        adminNotes = AdminNotes(
            notesForAdmin = selectedReservation.note,
            adminNote = selectedReservation.adminNote,
            adminNoteFormTitle = calculateAdminNoteFormTitle(selectedReservation),
            notesForAdminFormTitle = dictionary.getString(MR.strings.parking_reservation_item_additional_notes),
            adminNoteFormVisible = calculateAdminNoteFormVisibility(selectedReservation),
            additionNotesFormPlaceholderText = dictionary.getString(MR.strings.parking_reservation_additional_notes_empty_state),
        ),
        garageInfo = GarageInfo(
            garageLevel = calculateGarageLevel(selectedReservation),
            parkingSpot = calculateGarageSpot(selectedReservation),
            garageLevelVisible = selectedReservation.reservationStatus.status is Approved,
            parkingSpotVisible = selectedReservation.reservationStatus.status is Approved,
            garageLevelFormTitle = dictionary.getString(MR.strings.parking_reservation_item_garage_level),
            parkingSpotFormTitle = dictionary.getString(MR.strings.parking_reservation_item_parking_spot),
        ),
        cancelButton = CancelButton(
            cancelButtonLoading = false,
            cancelButtonVisible = calculateButtonVisibility(selectedReservation),
            cancelButtonText = dictionary.getString(MR.strings.my_parking_reservation_cancel_request),
        ),
        cancelRequestDialog = null,
    )

    private fun calculateGarageLevel(selectedReservation: ParkingReservationUiModel) =
        (selectedReservation.reservationStatus.status as? Approved)?.parkingCoordinate?.level?.title.orEmpty()

    private fun calculateGarageSpot(selectedReservation: ParkingReservationUiModel) =
        (selectedReservation.reservationStatus.status as? Approved)?.parkingCoordinate?.spot?.title.orEmpty()

    private fun calculateButtonVisibility(
        selectedReservation: ParkingReservationUiModel,
    ) = when (selectedReservation.reservationStatus.status) {
        is Approved, Submitted -> selectedReservation.dateTimeStamp.isTodayOrAfter()
        else -> false
    }

    private fun calculateAdminNoteFormTitle(selectedReservation: ParkingReservationUiModel) =
        if (selectedReservation.reservationStatus.status is Approved) {
            dictionary.getString(MR.strings.parking_reservation_item_approve_note)
        } else {
            dictionary.getString(MR.strings.parking_reservation_item_reject_reason)
        }

    private fun calculateAdminNoteFormVisibility(selectedReservation: ParkingReservationUiModel) =
        when (selectedReservation.reservationStatus.status) {
            is Approved, is Declined -> true
            is Canceled -> selectedReservation.adminNote.isNotEmpty()
            is Submitted -> false
        }
}
