package org.product.inventory.web.pages.seatreservationtmanagement

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import seatreservation.model.Office

data class SeatReservationManagementUiState(
    val title: String = "",
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val routeToNavigate: String? = null,
    val offices: List<OfficeUI> = emptyList(),
    val newOfficeName: String = "",
    val newOfficeNameErrorText: String? = null,
    val newOfficeNumberOfSeats: String = "",
    val validOfficeData: Boolean = false,
    val submitActive: Boolean = false,
    val isLoading: Boolean = true,
    val alertMessage: AlertMessage? = null,
    val officeLabel: String = "",
    val numberOfSeatsLabel: String = "",
    val newOfficeNamePlaceholder: String = "",
    val popupOptions: List<OfficePopupOptionUi> = emptyList(),
    val deleteInProgress: Boolean = false,
    val closeDeleteDialog: Boolean = false,
    val closeEditDialog: Boolean = false,
)

data class OfficeUI(
    val id: String,
    val name: String,
    val numberOfSeats: String,
)

data class OfficeDetailsData(
    val id: String,
    val detailsTitle: String,
    val name: String,
    val nameErrorText: String?,
    val numberOfSeats: String,
    val nameLabel: String,
    val numberOfSeatsLabel: String,
    val namePlaceholder: String,
    val numberOfSeatsPlaceholder: String,
    val submitActive: Boolean,
    val submitEnabled: Boolean,
    val buttonText: String,
)

data class OfficePopupOptionUi(
    val text: String,
    val icon: String,
    val option: OfficePopupOption,
)

enum class OfficePopupOption {
    EDIT,
    DELETE,
}

fun Office.toUI() = OfficeUI(
    id = id,
    name = title,
    numberOfSeats = numberOfSeats.toString(),
)

fun OfficeUI.toOffice() = Office(
    id = id,
    title = name,
    numberOfSeats = numberOfSeats.toInt(),
)
