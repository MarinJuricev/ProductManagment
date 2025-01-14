package parking.usersRequests.details.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.mapper.ParkingReservationStatusUiMapper
import parking.reservation.model.GarageLevel
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservationDetailsScreenTexts
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.reservation.model.ParkingReservationStatus.Canceled
import parking.reservation.model.ParkingReservationStatus.Declined
import parking.reservation.model.ParkingReservationStatus.Submitted
import parking.reservation.model.ParkingReservationStatusUiModel
import parking.reservation.model.ParkingReservationUiModel
import parking.reservation.model.ParkingSpot
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState
import parking.usersRequests.details.interaction.SelectedUserState
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageLevelUi.Deselected
import parking.usersRequests.details.model.GarageLevelUi.GarageLevelUiModel
import parking.usersRequests.details.model.GarageLevelUi.Undefined
import parking.usersRequests.details.model.GarageSpotUi
import parking.usersRequests.details.model.GarageSpotUi.GarageSpotUiModel
import parking.usersRequests.details.model.HasGarageAccessUi.Loading
import parking.usersRequests.details.model.HasGarageAccessUi.Retry
import parking.usersRequests.details.model.HasGarageAccessUi.UiData
import parking.usersRequests.details.model.ReservationDetailsEditableFields

class ParkingReservationDetailsScreenMapper(
    val dictionary: Dictionary,
    val statusUiMapper: ParkingReservationStatusUiMapper,
) {
    operator fun invoke(
        selectedParkingRequest: ParkingReservationUiModel,
        editableFields: ReservationDetailsEditableFields,
        availableGarageLevels: List<GarageLevelUi> = listOf(GarageLevelUi.Loading),
        selectedUser: SelectedUserState,
        screenTexts: ParkingReservationDetailsScreenTexts,
    ): ParkingReservationDetailsScreenState.Content {
        val currentStatus =
            editableFields.currentStatus ?: selectedParkingRequest.reservationStatus.status
        val adminNote = editableFields.adminNote ?: selectedParkingRequest.adminNote
        val currentGarageLevel = calculateCurrentGarageLevel(
            selectedParkingRequest = selectedParkingRequest,
            editableFields = editableFields,
        )
        val currentGarageSpot = calculateCurrentGarageSpot(
            selectedParkingRequest = selectedParkingRequest,
            editableFields = editableFields,
        )

        val hasGarageAccessUi = when (selectedUser) {
            is SelectedUserState.Error -> Retry(
                errorMessage = dictionary.getString(MR.strings.general_retry_error_title),
                buttonTitle = dictionary.getString(MR.strings.general_retry_button_text),
            )

            is SelectedUserState.Loading -> Loading
            is SelectedUserState.Received -> UiData(
                formTitle = dictionary.getString(MR.strings.user_request_garage_access_label),
                switchActive = editableFields.hasGarageAccess,
            )
        }

        val garageAccessSwitchVisible = calculateIsGarageAccessSwitchVisible(
            selectedParkingRequest,
            currentStatus,
            selectedUser,
        )

        return ParkingReservationDetailsScreenState.Content(
            screenTexts = screenTexts,
            currentStatus = statusUiMapper(currentStatus),
            adminNoteFormTitle = calculateAdminNoteFormTitle(currentStatus),
            availableStatusOptions = calculateAvailableStatusOptions(),
            adminNoteVisible = currentStatus is Approved || currentStatus is Declined,
            garageLevelPickersVisible = currentStatus is Approved && availableGarageLevels.isNotEmpty(),
            adminNote = adminNote,
            selectedReservation = selectedParkingRequest,
            saveButtonVisible = isSaveButtonVisible(
                currentStatus = currentStatus,
                selectedParkingRequest = selectedParkingRequest,
                adminNote = adminNote,
                availableLevels = availableGarageLevels,
                garageLevel = currentGarageLevel,
                currentGarageSpot = currentGarageSpot,
                editableFields = editableFields,
                switchVisible = garageAccessSwitchVisible,
            ),
            saveButtonLoading = editableFields.saveButtonLoading,
            availableGarageLevels = availableGarageLevels,
            currentGarageLevel = currentGarageLevel,
            currentGarageSpot = currentGarageSpot,
            availableGarageSpots = getAvailableGarageSpots(
                currentGarageLevel,
                availableGarageLevels,
            ),
            noSpaceLeftErrorVisible = availableGarageLevels.isEmpty(),
            garageAccessSwitchVisible = garageAccessSwitchVisible,
            hasGarageAccessUi = hasGarageAccessUi,
        )
    }

    private fun calculateIsGarageAccessSwitchVisible(
        selectedRequest: ParkingReservationUiModel,
        currentStatus: ParkingReservationStatus,
        user: SelectedUserState,
    ) =
        selectedRequest.isNotApproved() && currentStatus.isApproved() && user.hasNoPermanentGarageAccess()

    private fun SelectedUserState.hasNoPermanentGarageAccess() =
        (this is SelectedUserState.Received && !this.user.hasPermanentGarageAccess) || (this is SelectedUserState.Error)

    private fun getAvailableGarageSpots(
        currentGarageLevel: GarageLevelUi,
        availableGarageLevels: List<GarageLevelUi>,
    ): List<GarageSpotUi> =
        if (currentGarageLevel is GarageLevelUiModel) {
            availableGarageLevels.flatMap {
                if (it is GarageLevelUiModel && it.garageLevel.id == currentGarageLevel.garageLevel.id) {
                    it.spots
                } else {
                    listOf()
                }
            }
        } else {
            listOf()
        }

    private fun calculateAdminNoteFormTitle(currentStatus: ParkingReservationStatus?) =
        when (currentStatus) {
            is Approved -> dictionary.getString(MR.strings.parking_reservation_item_approve_note)
            is Canceled -> dictionary.getString(MR.strings.parking_reservation_item_cancel_reason)
            is Declined -> dictionary.getString(MR.strings.parking_reservation_item_reject_reason)
            is Submitted -> ""
            null -> ""
        }

    private fun calculateAvailableStatusOptions() = listOf(
        ParkingReservationStatusUiModel(
            status = Submitted,
            text = dictionary.getString(MR.strings.parking_reservation_status_submitted_label),
        ),
        ParkingReservationStatusUiModel(
            status = Approved(
                adminNote = "",
                parkingCoordinate = ParkingCoordinate(GarageLevel("", ""), ParkingSpot("", "")),
            ),
            text = dictionary.getString(MR.strings.parking_reservation_status_approved_label),
        ),
        ParkingReservationStatusUiModel(
            status = Declined(""),
            text = dictionary.getString(MR.strings.parking_reservation_status_rejected_label),
        ),
    )

    private fun calculateCurrentGarageSpot(
        selectedParkingRequest: ParkingReservationUiModel,
        editableFields: ReservationDetailsEditableFields,
    ): GarageSpotUi =
        if (editableFields.garageSpotUi == null && selectedParkingRequest.reservationStatus.status is Approved) {
            GarageSpotUiModel(spot = selectedParkingRequest.reservationStatus.status.parkingCoordinate.spot)
        } else {
            editableFields.garageSpotUi ?: GarageSpotUi.Undefined
        }

    private fun calculateCurrentGarageLevel(
        selectedParkingRequest: ParkingReservationUiModel,
        editableFields: ReservationDetailsEditableFields,
    ): GarageLevelUi = if (
        editableFields.garageLevelUi == null &&
        selectedParkingRequest.reservationStatus.status is Approved
    ) {
        val selectedLevel = selectedParkingRequest.reservationStatus.status.parkingCoordinate.level
        GarageLevelUiModel(
            title = selectedLevel.title,
            garageLevel = selectedLevel,
            spots = listOf(),
        )
    } else {
        editableFields.garageLevelUi ?: Deselected
    }

    private fun isSaveButtonVisible(
        currentStatus: ParkingReservationStatus?,
        selectedParkingRequest: ParkingReservationUiModel,
        adminNote: String,
        availableLevels: List<GarageLevelUi>,
        garageLevel: GarageLevelUi,
        currentGarageSpot: GarageSpotUi,
        editableFields: ReservationDetailsEditableFields,
        switchVisible: Boolean,
    ): Boolean = when {
        garageIsFull(currentStatus, availableLevels) ||
            levelUnselected(currentStatus, garageLevel) -> false

        garageAccessSwitchNotSelected(editableFields, currentStatus, switchVisible) -> false
        else -> editableFieldsChanged(
            currentStatus,
            selectedParkingRequest,
            adminNote,
            garageLevel,
            currentGarageSpot,
        )
    }

    private fun ParkingReservationUiModel.isNotApproved() =
        this.reservationStatus.status !is Approved

    private fun ParkingReservationStatus?.isApproved() = this is Approved

    private fun garageIsFull(
        currentStatus: ParkingReservationStatus?,
        availableGarageLevels: List<GarageLevelUi>,
    ) = currentStatus is Approved && availableGarageLevels.isEmpty()

    private fun levelUnselected(
        currentStatus: ParkingReservationStatus?,
        currentGarageLevel: GarageLevelUi,
    ) = currentStatus is Approved && currentGarageLevel is Deselected

    private fun garageAccessSwitchNotSelected(
        editableFields: ReservationDetailsEditableFields,
        status: ParkingReservationStatus?,
        switchVisible: Boolean,
    ) = !editableFields.hasGarageAccess && status is Approved && switchVisible

    private fun editableFieldsChanged(
        currentStatus: ParkingReservationStatus?,
        selectedParkingRequest: ParkingReservationUiModel,
        adminNote: String,
        garageLevel: GarageLevelUi,
        currentGarageSpot: GarageSpotUi,
    ): Boolean =
        statusChanged(currentStatus, selectedParkingRequest) ||
            adminNoteChanged(adminNote, selectedParkingRequest) ||
            garageLevelChanged(garageLevel) ||
            garageSpotChanged(currentGarageSpot)

    private fun statusChanged(
        currentStatus: ParkingReservationStatus?,
        selectedParkingRequest: ParkingReservationUiModel,
    ) = currentStatus != selectedParkingRequest.reservationStatus.status

    private fun adminNoteChanged(
        adminNote: String?,
        selectedParkingRequest: ParkingReservationUiModel,
    ) = adminNote != selectedParkingRequest.adminNote

    private fun garageLevelChanged(garageLevel: GarageLevelUi) =
        garageLevel != Undefined && garageLevel != Deselected

    private fun garageSpotChanged(garageSpot: GarageSpotUi) =
        garageSpot != GarageSpotUi.Undefined && garageSpot != GarageSpotUi.Deselected
}
