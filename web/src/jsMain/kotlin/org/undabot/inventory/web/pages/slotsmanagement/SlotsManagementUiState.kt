package org.product.inventory.web.pages.slotsmanagement

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.ConfirmationDialogState
import parking.reservation.model.ParkingSpot

data class SlotsManagementUiState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val isLoading: Boolean = true,
    val routeToNavigate: String? = null,
    val items: List<GarageLevelDataUi> = emptyList(),
    val popupOptions: List<SpotPopupOptionUi> = emptyList(),
    val createNewGarageLevelLabel: String = "",
    val deleteGarageLevelDialogState: ConfirmationDialogState? = null,
    val closeAddOrEditDialog: Boolean = false,
    val closeDeleteDialog: Boolean = false,
    val isDeleteActionInProgress: Boolean = false,
    val alertMessage: AlertMessage? = null,
)

data class GarageLevelDataUi(
    val id: String,
    val title: String,
    val spots: List<ParkingSpotUi>,
    val registeredSpotsLabel: String,
    val levelLabel: String,
)

data class ParkingSpotUi(
    val id: String,
    val originalTitle: String,
    val displayedTitle: String,
)

data class SpotPopupOptionUi(
    val icon: String,
    val text: String,
    val option: SpotPopupOption,
)

data class UiStateFlags(
    val isLoading: Boolean = true,
    val isDeleteActionInProgress: Boolean = false,
    val closeAddOrEditDialog: Boolean = false,
    val closeDeleteDialog: Boolean = false,
)

enum class SpotPopupOption {
    EDIT,
    DELETE,
}

sealed interface GarageLevelUpsertMode {
    data object New : GarageLevelUpsertMode
    data class Edit(val garageLevelId: String) : GarageLevelUpsertMode
}

data class GarageLevelDetailsState(
    val titleLabel: String,
    val titlePlaceholder: String,
    val title: String,
    val spotsLabel: String,
    val spots: List<ParkingSpotUi>,
    val newSpotNamePlaceholder: String,
    val newSpotName: String,
    val newSpotNameValid: Boolean,
    val isLoading: Boolean,
    val saveButtonText: String,
    val saveEnabled: Boolean,
)

fun ParkingSpotUi.toParkingSpot() = ParkingSpot(
    id = id,
    title = originalTitle,
)
