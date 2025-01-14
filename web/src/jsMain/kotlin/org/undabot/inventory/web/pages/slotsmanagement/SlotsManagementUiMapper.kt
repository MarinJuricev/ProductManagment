package org.product.inventory.web.pages.slotsmanagement

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.ConfirmationDialogState
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot

class SlotsManagementUiMapper(
    private val dictionary: Dictionary,
) {

    private data class StateStaticData(
        val breadcrumbItems: List<BreadcrumbItem>,
        val title: String,
        val popupOptions: List<SpotPopupOptionUi>,
        val createNewGarageLevelLabel: String,
    )

    private data class DetailsStateStaticData(
        val titleLabel: String,
        val titlePlaceholder: String,
        val spotsLabel: String,
        val newSpotNamePlaceholder: String,
        val saveButtonText: String,
    )

    private val stateStaticData by lazy {
        StateStaticData(
            breadcrumbItems = buildBreadcrumbItems(),
            title = dictionary.get(StringRes.slotsManagementTitle),
            popupOptions = buildPopupOptions(),
            createNewGarageLevelLabel = dictionary.get(StringRes.slotsManagementNewGarageLevelLabel),
        )
    }

    fun toUiState(
        uiStateFlags: UiStateFlags,
        routeToNavigate: String?,
        garageLevels: List<GarageLevelData>,
        garageLevelForDeletion: String?,
        alertMessage: AlertMessage?,
    ) = SlotsManagementUiState(
        breadcrumbItems = stateStaticData.breadcrumbItems,
        title = stateStaticData.title,
        isLoading = uiStateFlags.isLoading,
        routeToNavigate = routeToNavigate,
        items = garageLevels.map { it.toGarageLevelDataUi() },
        popupOptions = stateStaticData.popupOptions,
        createNewGarageLevelLabel = stateStaticData.createNewGarageLevelLabel,
        deleteGarageLevelDialogState = garageLevelForDeletion?.let(::buildDeleteGarageLevelDialogState),
        closeAddOrEditDialog = uiStateFlags.closeAddOrEditDialog,
        closeDeleteDialog = uiStateFlags.closeDeleteDialog,
        isDeleteActionInProgress = uiStateFlags.isDeleteActionInProgress,
        alertMessage = alertMessage,
    )

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.slotsManagementPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.slotsManagementPath2),
            route = Routes.parkingReservation,
            isNavigable = true,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.slotsManagementPath3),
            route = Routes.crewManagement,
        ),
    )

    private fun buildPopupOptions() = listOf(
        SpotPopupOptionUi(
            icon = ImageRes.editIcon,
            text = dictionary.get(StringRes.slotsManagementPopupOptionEdit),
            option = SpotPopupOption.EDIT,
        ),
        SpotPopupOptionUi(
            icon = ImageRes.deleteIcon,
            text = dictionary.get(StringRes.slotsManagementPopupOptionDelete),
            option = SpotPopupOption.DELETE,
        ),
    )

    private fun GarageLevelData.toGarageLevelDataUi() = GarageLevelDataUi(
        id = id,
        title = level.title,
        spots = spots.map { it.toParkingSpotUi() },
        registeredSpotsLabel = dictionary.get(StringRes.slotsManagementRegisteredSpotsLabel),
        levelLabel = dictionary.get(StringRes.slotsManagementLevelLabel).uppercase(),
    )

    private fun ParkingSpot.toParkingSpotUi() = ParkingSpotUi(
        id = id,
        originalTitle = title,
        displayedTitle = dictionary.get(StringRes.slotsManagementSpotLabel, title),
    )

    private fun buildDeleteGarageLevelDialogState(garageLevelId: String) = ConfirmationDialogState(
        title = dictionary.get(StringRes.slotsManagementDeleteGarageLevelDialogTitle, garageLevelId),
        message = dictionary.get(StringRes.slotsManagementDeleteGarageLevelDialogMessage, garageLevelId),
        positiveText = dictionary.get(StringRes.slotsManagementDeleteGarageLevelDialogPositiveText),
        negativeText = dictionary.get(StringRes.slotsManagementDeleteGarageLevelDialogNegativeText),
    )

    private val detailsStateStaticData by lazy {
        DetailsStateStaticData(
            titleLabel = dictionary.get(StringRes.slotsManagementDetailsTitleLabel),
            titlePlaceholder = dictionary.get(StringRes.slotsManagementDetailsTitlePlaceholder),
            spotsLabel = dictionary.get(StringRes.slotsManagementDetailsSpotsLabel),
            newSpotNamePlaceholder = dictionary.get(StringRes.slotsManagementDetailsNewSpotNamePlaceholder),
            saveButtonText = dictionary.get(StringRes.slotsManagementDetailsSaveButtonText),
        )
    }

    fun toDetailsUiState(
        title: String,
        spots: List<ParkingSpot>,
        newSpotName: String,
        isLoading: Boolean,
        otherGarageLevelNames: List<String>,
    ) = GarageLevelDetailsState(
        title = title,
        spots = spots.map { it.toParkingSpotUi() },
        newSpotName = newSpotName,
        newSpotNameValid = newSpotName.isNotBlank() && spots.none { spot -> spot.title == newSpotName.trim() },
        isLoading = isLoading,
        titleLabel = detailsStateStaticData.titleLabel,
        titlePlaceholder = detailsStateStaticData.titlePlaceholder,
        spotsLabel = detailsStateStaticData.spotsLabel,
        newSpotNamePlaceholder = detailsStateStaticData.newSpotNamePlaceholder,
        saveButtonText = detailsStateStaticData.saveButtonText,
        saveEnabled = title.isNotBlank() && otherGarageLevelNames.none { garageLevel -> garageLevel == title.trim() } && spots.isNotEmpty(),
    )
}
