package org.product.inventory.web.pages.newrequest

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.isValid
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus

class NewRequestUiMapper(
    private val dictionary: Dictionary,
    private val newRequestStaticDataMapper: NewRequestStaticDataMapper,
    private val parkingSelectionDataMapper: ParkingSelectionDataMapper,
) {

    fun buildUiState(
        editableFields: NewRequestEditableFields,
        events: NewRequestEvents,
        alertMessage: AlertMessage?,
        selectedUser: UserListItem,
        requestMode: RequestMode,
        garageLevelAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
    ) = NewRequestState(
        staticData = newRequestStaticDataMapper.staticData,
        profileInfo = selectedUser.profileInfo,
        date = editableFields.date,
        notesLabel = buildNotesLabel(requestMode),
        notesPlaceholder = buildNotesPlaceholder(requestMode),
        notes = editableFields.notes,
        submitEnabled = buildSubmitEnabled(requestMode, editableFields),
        isLoading = events.isLoading,
        alertMessage = alertMessage,
        routeToNavigate = events.routeToNavigate,
        lateReservationMessage = buildLateReservationMessage(events.isLateReservation),
        requestMode = requestMode,
        userListFilterText = editableFields.userListFilterText,
        closeDialog = events.closeDialog,
        garageAccess = editableFields.garageAccess,
        garageLevelAndParkingSpotsStatus = garageLevelAndParkingSpotsStatus,
        parkingSelectionData = parkingSelectionDataMapper(
            garageLevelsAndParkingSpotsStatus = garageLevelAndParkingSpotsStatus,
            editableFields = editableFields,
        ),
    )

    private fun buildNotesPlaceholder(requestMode: RequestMode): String = when (requestMode) {
        is RequestMode.Automatic -> ""
        is RequestMode.Basic -> newRequestStaticDataMapper.staticData.additionalNotesPlaceholder
    }

    private fun buildNotesLabel(requestMode: RequestMode) = when (requestMode) {
        is RequestMode.Automatic -> dictionary.get(StringRes.parkingReservationItemApproveNote)
        is RequestMode.Basic -> dictionary.get(StringRes.parkingReservationNewRequestAdditionalNotes)
    }

    private fun buildLateReservationMessage(isLateReservation: Boolean) = when (isLateReservation) {
        false -> null
        true -> dictionary.get(
            StringRes.newRequestLateReservationMessage,
            FACILITY_EMAIL,
            FACILITY_SLACK_CHANNEL,
        )
    }

    private fun buildSubmitEnabled(
        requestMode: RequestMode,
        editableFields: NewRequestEditableFields,
    ) = when (requestMode) {
        is RequestMode.Basic -> requestMode.hasSelectedRequestDates
        is RequestMode.Automatic -> with(editableFields) {
            selectedGarageLevel.isValid() && selectedParkingSpot.isValid() && garageAccess
        }
    }
}

private const val FACILITY_EMAIL = "facility@MarinJuricev.com"
private const val FACILITY_SLACK_CHANNEL = "#facility-support"
