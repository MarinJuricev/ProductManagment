package org.product.inventory.web.pages.slotsmanagement

sealed interface SlotsManagementEvent {

    data class PathClick(val path: String) : SlotsManagementEvent

    data class OnAddOrEditClick(val garageLevelId: String? = null) : SlotsManagementEvent

    data class OnDeleteClick(val garageLevelDataUi: GarageLevelDataUi) : SlotsManagementEvent

    data class OnGarageTitleChanged(val text: String) : SlotsManagementEvent

    data class OnNewSpotNameChanged(val text: String) : SlotsManagementEvent

    data class OnDeleteSpotClick(val parkingSpotUi: ParkingSpotUi) : SlotsManagementEvent

    data class OnSaveClick(
        val title: String,
        val spots: List<ParkingSpotUi>,
    ) : SlotsManagementEvent

    data object DetailsClosed : SlotsManagementEvent

    data object OnAddNewSpotClick : SlotsManagementEvent

    data object OnPositiveClickDeleteDialog : SlotsManagementEvent

    data object OnNegativeClickDeleteDialog : SlotsManagementEvent

    data object DeleteDialogClosed : SlotsManagementEvent
}
