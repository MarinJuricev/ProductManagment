package parking.reservation.mapper

import core.utils.DateProvider
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.interaction.CreateParkingReservationScreenState.Content
import parking.reservation.interaction.DatePickerData
import parking.reservation.interaction.GarageDataUI
import parking.reservation.interaction.HeaderData
import parking.reservation.interaction.NotesFormData
import parking.reservation.interaction.SubmitButtonData
import parking.reservation.model.GarageLevelsAndSpotsStatus
import parking.reservation.model.GarageLevelsAndSpotsStatus.Failure
import parking.reservation.model.GarageLevelsAndSpotsStatus.Loading
import parking.reservation.model.GarageLevelsAndSpotsStatus.Success
import parking.reservation.model.MultiDateSelectionState
import parking.reservation.model.NewRequestEditableFields
import parking.reservation.model.RequestMode
import parking.reservation.model.RequestMode.Request
import parking.reservation.model.RequestMode.Reservation
import parking.usersRequests.details.model.GarageLevelUi.GarageLevelUiModel
import parking.usersRequests.details.model.GarageSpotUi.GarageSpotUiModel
import user.model.InventoryAppUser
import utils.convertMillisToDate

class CreateParkingReservationUiMapper(
    private val dictionary: Dictionary,
    private val currentDateProvider: DateProvider,
) {

    private data class StateStaticData(
        val headerTitle: String = "",
        val datePickerFormTitle: String = "",
        val datePickerConfirmButtonText: String = "",
        val additionalNotesForAdminFormTitle: String = "",
        val approveNotesFormTitle: String = "",
        val additionalNotesForAdminPlaceholder: String = "",
        val approveNotesPlaceholder: String = "",
        val garageAccessFormTitle: String = "",
        val submitButtonTitle: String = "",
        val switchFormTitle: String = "",
        val garageLevelPickerTitle: String = "",
        val garageSpotPickerTitle: String = "",
        val noFreeSpaceMessage: String = "",
    )

    private val staticData by lazy {
        StateStaticData(
            headerTitle = dictionary.getString(MR.strings.parking_reservation_new_request_as),
            datePickerFormTitle = dictionary.getString(MR.strings.parking_reservation_new_request_date),
            datePickerConfirmButtonText = dictionary.getString(MR.strings.general_ok),
            additionalNotesForAdminFormTitle = dictionary.getString(MR.strings.parking_reservation_new_request_additional_notes),
            approveNotesFormTitle = dictionary.getString(MR.strings.parking_reservation_item_approve_note),
            additionalNotesForAdminPlaceholder = dictionary.getString(MR.strings.parking_reservation_new_request_additional_notes_placeholder),
            approveNotesPlaceholder = dictionary.getString(MR.strings.parking_reservation_new_request_additional_notes_placeholder),
            garageAccessFormTitle = dictionary.getString(MR.strings.user_request_garage_access_label),
            submitButtonTitle = dictionary.getString(MR.strings.parking_reservation_new_request_submit),
            switchFormTitle = dictionary.getString(MR.strings.user_request_garage_access_label),
            garageLevelPickerTitle = dictionary.getString(MR.strings.parking_reservation_item_garage_level),
            garageSpotPickerTitle = dictionary.getString(MR.strings.parking_reservation_item_parking_spot),
            noFreeSpaceMessage = dictionary.getString(MR.strings.parking_reservation_no_free_spaces_error),
        )
    }

    fun map(
        selectedUser: InventoryAppUser,
        requestMode: RequestMode,
        editableFields: NewRequestEditableFields,
        garageDataStatus: GarageLevelsAndSpotsStatus,
        multiDateSelectionState: MultiDateSelectionState,
    ): Content = Content(
        requestMode = requestMode,
        headerData = HeaderData(
            user = selectedUser,
            headerTitle = staticData.headerTitle,
            selectAnotherUserEnabled = requestMode is Reservation,
        ),
        datePickerData = DatePickerData(
            datePickerFormTitle = staticData.datePickerFormTitle,
            datePickerConfirmButtonText = staticData.datePickerConfirmButtonText,
            displayedDate = convertMillisToDate(editableFields.selectedDate),
            selectedTimestamp = editableFields.selectedDate,
            lowerDateLimit = currentDateProvider.generateDate(),
        ),
        notesFormData = NotesFormData(
            notesFormTitle = generateNotesFormTitle(requestMode),
            notesPlaceholderText = generateNotesPlaceholder(requestMode),
            notes = editableFields.notes,
        ),
        garageData = calculateGarageData(garageDataStatus, editableFields, selectedUser),
        submitButtonData = SubmitButtonData(
            buttonEnabled = calculateIsButtonEnabled(requestMode, editableFields, selectedUser, multiDateSelectionState),
            submitButtonText = staticData.submitButtonTitle,
            submitButtonLoading = editableFields.submitButtonLoading,
        ),
        multiDatePickerState = multiDateSelectionState,
    )

    private fun generateNotesFormTitle(requestMode: RequestMode) = when (requestMode) {
        is Reservation -> staticData.approveNotesFormTitle
        is Request -> staticData.additionalNotesForAdminFormTitle
    }

    private fun generateNotesPlaceholder(requestMode: RequestMode) = when (requestMode) {
        is Reservation -> staticData.approveNotesPlaceholder
        is Request -> staticData.additionalNotesForAdminPlaceholder
    }

    private fun calculateIsButtonEnabled(
        requestMode: RequestMode,
        editableFields: NewRequestEditableFields,
        user: InventoryAppUser,
        multiDateSelectionState: MultiDateSelectionState,
    ) = when (requestMode) {
        is Reservation -> isParkingSelected(editableFields) &&
            userHasGarageAccess(user, editableFields)

        is Request -> multiDateSelectionState.selectedDates.isNotEmpty()
    }

    private fun isParkingSelected(editableFields: NewRequestEditableFields) =
        editableFields.selectedGarageSpot is GarageSpotUiModel

    private fun userHasGarageAccess(
        user: InventoryAppUser,
        editableFields: NewRequestEditableFields,
    ) = user.hasPermanentGarageAccess || editableFields.hasGarageAccess

    private fun calculateGarageData(
        garageData: GarageLevelsAndSpotsStatus,
        editableFields: NewRequestEditableFields,
        selectedUser: InventoryAppUser,
    ) = when (garageData) {
        is Loading -> GarageDataUI.Loading
        is Failure -> GarageDataUI.Retry
        is Success -> GarageDataUI.Content(
            garageLevelPickerTitle = staticData.garageLevelPickerTitle,
            garageSpotPickerTitle = staticData.garageSpotPickerTitle,
            noFreeSpaceMessage = staticData.noFreeSpaceMessage,
            availableGarageLevels = garageData.levels,
            availableGarageSpots = calculateAvailableSpots(editableFields),
            currentGarageLevel = editableFields.selectedGarageLevel,
            currentGarageSpot = editableFields.selectedGarageSpot,
            noSpaceLeftErrorVisible = garageData.levels.isEmpty(),
            garageAccessSwitchVisible = !selectedUser.hasPermanentGarageAccess,
            switchFormTitle = staticData.switchFormTitle,
            hasGarageAccess = editableFields.hasGarageAccess,
        )
    }

    private fun calculateAvailableSpots(editableFields: NewRequestEditableFields) =
        when (editableFields.selectedGarageLevel) {
            is GarageLevelUiModel -> editableFields.selectedGarageLevel.spots
            else -> listOf()
        }
}
