package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.components.isValid
import org.product.inventory.web.components.orEmpty
import org.product.inventory.web.components.toDateInputValue
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.datetime.formatted
import org.product.inventory.web.datetime.toLocalDate
import org.product.inventory.web.datetime.toLocalDateTime
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.models.ParkingSelectionData
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi
import org.product.inventory.web.pages.myreservations.toParkingReservationStatusUi
import parking.reservation.model.GarageLevel
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingSpot

class UserRequestsUiMapper(
    private val dictionary: Dictionary,
) {

    private val statusFilterEntries = buildList {
        add(null) // All statuses option

        val statuses = buildList {
            addAll(buildParkingReservationStatuses())
            add(ParkingReservationStatus.Canceled)
        }

        addAll(statuses.map(ParkingReservationStatus::toParkingReservationStatusUi))
    }

    fun buildUiState(
        userRequests: List<ParkingReservation>,
        headerData: UserRequestsHeaderData,
        closeDialog: Boolean,
        isLoading: Boolean,
        alertMessage: AlertMessage?,
        routeToNavigate: String?,
    ) = UserRequestsState(
        breadcrumbItems = buildBreadcrumbItems(),
        title = dictionary.get(StringRes.userRequestsTitle),
        fromDateLabel = dictionary.get(StringRes.parkingReservationItemFrom),
        toDateLabel = dictionary.get(StringRes.parkingReservationItemTo),
        requests = userRequests.map { item -> item.toUserRequestItemUi() },
        emptyRequestsMessage = dictionary.get(StringRes.myParkingReservationEmpty),
        fromDate = headerData.dateRange.fromDate.toDateInputValue(),
        toDate = headerData.dateRange.toDate.toDateInputValue(),
        statusFilterEntries = statusFilterEntries,
        userEmailFilter = headerData.filterData.userEmail,
        userEmailFilterPlaceholder = dictionary.get(StringRes.userRequestsUserEmailFilterPlaceholder),
        userEmailFilterLabel = dictionary.get(StringRes.userRequestsUserEmailFilterLabel),
        statusFilter = headerData.filterData.status,
        statusFilterAllOptionText = dictionary.get(StringRes.userRequestsStatusFilterAllOptionText),
        closeDialog = closeDialog,
        isLoading = isLoading,
        alertMessage = alertMessage,
        routeToNavigate = routeToNavigate,
    )

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.userRequestsPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.userRequestsPath2),
            route = Routes.parkingReservation,
            isNavigable = true,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.userRequestsPath3),
            route = Routes.userRequests,
        ),
    )

    private fun ParkingReservation.toUserRequestItemUi() = UserRequestItemUi(
        id = id,
        requestedDate = date.toLocalDate().formatted(),
        requestedDateMillis = date,
        status = status.toParkingReservationStatusUi(),
        requestDateLabel = dictionary.get(StringRes.parkingReservationItemRequestedDate),
        emailLabel = dictionary.get(StringRes.userRequestsEmailLabel),
        email = email,
        submittedDateLabel = dictionary.get(StringRes.userRequestsSubmittedDateLabel),
        submittedDate = createdAt.toLocalDateTime().formatted(),
    )

    fun buildUserRequestDetailsItem(
        parkingReservation: ParkingReservation,
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
        editableFields: EditableFields,
        id: String,
        savingActive: Boolean,
        permanentGarageAccess: Boolean,
    ): UserRequestDetailsState.UserRequestDetailsItemUi {
        val currentStatus = parkingReservation.status.toParkingReservationStatusUi()
        val updatedStatus = editableFields.statusUi ?: currentStatus
        val selectedGarageLevel = buildSelectedGarageLevel(currentStatus, editableFields)

        return UserRequestDetailsState.UserRequestDetailsItemUi(
            id = id,
            requestedDateDateInput = parkingReservation.date.toDateInputValue(),
            note = parkingReservation.note,
            status = updatedStatus,
            requestedDateLabel = dictionary.get(StringRes.userRequestsRequestedDateLabel),
            submittedDateLabel = dictionary.get(StringRes.userRequestsSubmittedDateLabel),
            submittedDate = parkingReservation.createdAt.toLocalDateTime().formatted(),
            rejectReasonLabel = dictionary.get(StringRes.userRequestsRejectReasonLabel),
            adminNotesLabel = dictionary.get(StringRes.userRequestsAdminNotesLabel),
            saveButtonText = dictionary.get(StringRes.userRequestsSaveButtonText),
            approveNoteLabel = dictionary.get(StringRes.userRequestsApproveNoteLabel),
            savingActive = savingActive,
            saveVisible = isSaveVisible(currentStatus, updatedStatus, savingActive),
            statusDropdownEntries = buildParkingReservationStatuses()
                .map(ParkingReservationStatus::toParkingReservationStatusUi),
            garageLevelAndParkingSpotsStatus = garageLevelsAndParkingSpotsStatus,
            approveNote = when (updatedStatus) {
                is ParkingReservationStatusUi.Approved ->
                    editableFields.approveNote ?: updatedStatus.adminNote
                else -> ""
            },
            rejectReason = when (updatedStatus) {
                is ParkingReservationStatusUi.Rejected ->
                    editableFields.rejectReason ?: updatedStatus.adminNote
                else -> ""
            },
            parkingSelectionData = buildParkingSelectionData(
                currentStatus = currentStatus,
                updatedStatus = updatedStatus,
                garageLevelsAndParkingSpotsStatus = garageLevelsAndParkingSpotsStatus,
                editableFields = editableFields,
                selectedGarageLevel = selectedGarageLevel,
            ),
            hasPermanentGarageAccessLabel = dictionary.get(StringRes.userRequestsGarageAccessLabel),
            hasPermanentGarageAccess = editableFields.hasPermanentGarageAccess,
            garageAccessSwitchVisible = buildGarageAccessSwitchVisibility(permanentGarageAccess, currentStatus, updatedStatus),
        )
    }

    private fun buildGarageAccessSwitchVisibility(
        permanentGarageAccess: Boolean,
        currentStatus: ParkingReservationStatusUi,
        updatedStatus: ParkingReservationStatusUi,
    ) = when {
        permanentGarageAccess || currentStatus is ParkingReservationStatusUi.Approved -> false
        updatedStatus is ParkingReservationStatusUi.Approved -> true
        else -> false
    }

    private fun isSaveVisible(
        currentStatus: ParkingReservationStatusUi,
        updatedStatus: ParkingReservationStatusUi,
        savingActive: Boolean,
    ): Boolean = when {
        savingActive -> true
        updatedStatus is ParkingReservationStatusUi.Canceled -> false
        else -> !(currentStatus is ParkingReservationStatusUi.Submitted && currentStatus == updatedStatus)
    }

    private fun buildParkingSelectionData(
        currentStatus: ParkingReservationStatusUi,
        updatedStatus: ParkingReservationStatusUi,
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
        editableFields: EditableFields,
        selectedGarageLevel: ParkingOption,
    ) = ParkingSelectionData(
        garageLevelLabel = dictionary.get(StringRes.userRequestsGarageLevelLabel),
        parkingSpotLabel = dictionary.get(StringRes.userRequestsParkingSpotLabel),
        parkingErrorMessage = dictionary.get(StringRes.userRequestsParkingErrorMessage),
        parkingErrorRetryMessage = dictionary.get(StringRes.userRequestsParkingErrorMessageRetry),
        noParkingSpacesMessage = dictionary.get(StringRes.userRequestsNoParkingSpacesMessage),
        garageLevels = buildGarageLevels(garageLevelsAndParkingSpotsStatus),
        parkingSpots = buildParkingSpots(garageLevelsAndParkingSpotsStatus, selectedGarageLevel.id),
        selectedGarageLevel = selectedGarageLevel,
        selectedParkingSpot = buildSelectedParkingSpot(currentStatus, editableFields),
        validParkingOption = when (updatedStatus) {
            is ParkingReservationStatusUi.Approved -> editableFields.run {
                garageLevel.isValid() && parkingSpot.isValid() && hasPermanentGarageAccess
            }
            else -> true
        },
    )

    private fun buildSelectedGarageLevel(
        status: ParkingReservationStatusUi,
        editableFields: EditableFields,
    ) = when (status) {
        is ParkingReservationStatusUi.Approved -> editableFields.garageLevel ?: status.garageLevel
        else -> editableFields.garageLevel.orEmpty()
    }

    private fun buildSelectedParkingSpot(
        status: ParkingReservationStatusUi,
        editableFields: EditableFields,
    ) = when (status) {
        is ParkingReservationStatusUi.Approved -> editableFields.parkingSpot ?: status.parkingSpot
        else -> editableFields.parkingSpot.orEmpty()
    }

    private fun buildGarageLevels(
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
    ) = when (garageLevelsAndParkingSpotsStatus) {
        is GarageLevelAndParkingSpotsStatus.Success -> garageLevelsAndParkingSpotsStatus.garageLevelData.map {
            ParkingOption(value = it.level.title, id = it.id)
        }
        else -> emptyList()
    }

    private fun buildParkingSpots(
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
        selectedGarageLevelId: String,
    ): List<ParkingOption> {
        if (
            garageLevelsAndParkingSpotsStatus !is GarageLevelAndParkingSpotsStatus.Success ||
            selectedGarageLevelId == ParkingOption.INVALID_VALUE
        ) {
            return emptyList()
        }

        return garageLevelsAndParkingSpotsStatus.garageLevelData
            .firstOrNull { it.id == selectedGarageLevelId }
            ?.spots
            ?.map { ParkingOption(value = it.title, id = it.id) }
            ?: emptyList()
    }

    private fun buildParkingReservationStatuses() = listOf(
        ParkingReservationStatus.Submitted,
        ParkingReservationStatus.Declined(adminNote = ""),
        ParkingReservationStatus.Approved(
            adminNote = "",
            parkingCoordinate = ParkingCoordinate(
                level = GarageLevel(
                    id = "",
                    title = "",
                ),
                spot = ParkingSpot(
                    id = "",
                    title = "",
                ),
            ),
        ),
    )
}
