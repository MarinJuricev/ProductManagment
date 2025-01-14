package parking.slotsManagement.interaction

import components.ImageType
import components.QuestionDialogData
import parking.reservation.model.GarageLevelData

sealed interface SlotsManagementScreenState {
    data object Loading : SlotsManagementScreenState
    data class Content(
        val screenTitle: String,
        val itemLevelIdentifier: String,
        val numberOfSpotsIdentifier: String,
        val levels: List<GarageLevelData>,
        val availableOptions: List<GarageLevelOption>,
        val leadingSpotIdentifierText: String,
        val levelCreatorVisible: Boolean,
        val selectedGarageLevel: GarageLevelData?,
        val dialogData: QuestionDialogData,
        val itemMenuIconResource: ImageType.Resource,
    ) : SlotsManagementScreenState
}

sealed interface GarageLevelOption {
    val displayedText: String
    val iconResource: ImageType.Resource
    data class Delete(
        override val displayedText: String,
        override val iconResource: ImageType.Resource,
    ) : GarageLevelOption

    data class Edit(
        override val displayedText: String,
        override val iconResource: ImageType.Resource,
    ) : GarageLevelOption
}
