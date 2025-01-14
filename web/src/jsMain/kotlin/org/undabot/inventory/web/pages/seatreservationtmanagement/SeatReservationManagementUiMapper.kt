package org.product.inventory.web.pages.seatreservationtmanagement

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.ConfirmationDialogState
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import seatreservation.model.Office

class SeatReservationManagementUiMapper(
    private val dictionary: Dictionary,
) {

    private data class SeatReservationManagementStaticData(
        val title: String,
        val breadcrumbItems: List<BreadcrumbItem>,
        val officeLabel: String,
        val numberOfSeatsLabel: String,
        val newOfficeNamePlaceholder: String,
        val popupOptions: List<OfficePopupOptionUi>,
        val editDialogTitle: String,
        val editDialogNameLabel: String,
        val editDialogNumberOfSeatsLabel: String,
        val editDialogButtonText: String,
        val editDialogNamePlaceholder: String,
    )

    private val staticData by lazy {
        SeatReservationManagementStaticData(
            title = dictionary.get(StringRes.seatReservationManagementTitle),
            breadcrumbItems = buildBreadcrumbItems(),
            officeLabel = dictionary.get(StringRes.seatReservationManagementOfficeLabel),
            numberOfSeatsLabel = dictionary.get(StringRes.seatReservationManagementNumberOfSeatsLabel),
            newOfficeNamePlaceholder = dictionary.get(StringRes.seatReservationManagementNewOfficeNamePlaceholder),
            popupOptions = buildPopupOptions(),
            editDialogTitle = dictionary.get(StringRes.seatReservationManagementEditDialogTitle),
            editDialogNameLabel = dictionary.get(StringRes.seatReservationManagementEditDialogNameLabel),
            editDialogNumberOfSeatsLabel = dictionary.get(StringRes.seatReservationManagementEditDialogNumberOfSeatsLabel),
            editDialogButtonText = dictionary.get(StringRes.seatReservationManagementEditDialogButtonText),
            editDialogNamePlaceholder = dictionary.get(StringRes.seatReservationManagementEditDialogNamePlaceholder),
        )
    }

    fun toUiState(
        offices: List<Office>,
        editableFields: SeatReservationManagementEditableFields,
        events: SeatReservationManagementEvents,
        alertMessage: AlertMessage?,
        officeNameAlreadyExists: Boolean,
    ): SeatReservationManagementUiState = SeatReservationManagementUiState(
        title = staticData.title,
        breadcrumbItems = staticData.breadcrumbItems,
        routeToNavigate = events.routeToNavigate,
        offices = offices.map(Office::toUI),
        newOfficeName = editableFields.newOfficeName,
        newOfficeNameErrorText = buildNewOfficeNameErrorText(officeNameAlreadyExists),
        newOfficeNumberOfSeats = editableFields.newOfficeNumberOfSeats,
        validOfficeData = validateOfficeData(
            tempOfficeName = editableFields.newOfficeName,
            tempOfficeNumberOfSeats = editableFields.newOfficeNumberOfSeats,
            officeNameAlreadyExists = officeNameAlreadyExists,
        ),
        submitActive = events.submitActive,
        isLoading = events.isLoading,
        alertMessage = alertMessage,
        officeLabel = staticData.officeLabel,
        numberOfSeatsLabel = staticData.numberOfSeatsLabel,
        newOfficeNamePlaceholder = staticData.newOfficeNamePlaceholder,
        popupOptions = staticData.popupOptions,
        deleteInProgress = events.deleteInProgress,
        closeDeleteDialog = events.closeDeleteDialog,
        closeEditDialog = events.closeEditDialog,
    )

    private fun validateOfficeData(
        tempOfficeName: String,
        tempOfficeNumberOfSeats: String,
        officeNameAlreadyExists: Boolean,
    ) = (OFFICE_MIN_LENGTH..OFFICE_MAX_LENGTH).contains(tempOfficeName.trim().length) &&
        tempOfficeNumberOfSeats.isNotBlank() &&
        !officeNameAlreadyExists

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.seatReservationManagementPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.seatReservationManagementPath2),
            route = Routes.seatReservation,
        ),
    )

    private fun buildPopupOptions() = listOf(
        OfficePopupOptionUi(
            text = dictionary.get(StringRes.seatReservationManagementPopupOptionEdit),
            icon = ImageRes.editIcon,
            option = OfficePopupOption.EDIT,
        ),
        OfficePopupOptionUi(
            text = dictionary.get(StringRes.seatReservationManagementPopupOptionDelete),
            icon = ImageRes.deleteIcon,
            option = OfficePopupOption.DELETE,
        ),
    )

    fun toDeleteOfficeData(officeUI: OfficeUI) = ConfirmationDialogState(
        title = dictionary.get(StringRes.seatReservationManagementDeleteDialogTitle),
        message = dictionary.get(StringRes.seatReservationManagementDeleteDialogMessage, officeUI.name),
        positiveText = dictionary.get(StringRes.seatReservationManagementDeleteDialogPositiveText),
        negativeText = dictionary.get(StringRes.seatReservationManagementDeleteDialogNegativeText),
    )

    fun toOfficeDetailsData(
        officeId: String,
        officeName: String,
        officeNumberOfSeats: String,
        submitActive: Boolean,
        officeNameAlreadyExists: Boolean,
    ) = OfficeDetailsData(
        id = officeId,
        name = officeName,
        numberOfSeats = officeNumberOfSeats,
        detailsTitle = staticData.editDialogTitle,
        nameLabel = staticData.editDialogNameLabel,
        numberOfSeatsLabel = staticData.editDialogNumberOfSeatsLabel,
        buttonText = staticData.editDialogButtonText,
        namePlaceholder = staticData.editDialogNamePlaceholder,
        numberOfSeatsPlaceholder = "-",
        submitActive = submitActive,
        submitEnabled = (OFFICE_MIN_LENGTH..OFFICE_MAX_LENGTH).contains(officeName.trim().length) &&
            officeNumberOfSeats.isNotBlank() &&
            !officeNameAlreadyExists,
        nameErrorText = buildNewOfficeNameErrorText(officeNameAlreadyExists),
    )

    private fun buildNewOfficeNameErrorText(officeAlreadyExists: Boolean) = when (officeAlreadyExists) {
        true -> dictionary.get(StringRes.seatReservationManagementOfficeNameExistsError)
        false -> null
    }
}

private const val OFFICE_MIN_LENGTH = 3
private const val OFFICE_MAX_LENGTH = 20
