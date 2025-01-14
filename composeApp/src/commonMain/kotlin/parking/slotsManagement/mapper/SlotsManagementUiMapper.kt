package parking.slotsManagement.mapper

import components.ImageType
import components.QuestionDialogData
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.GarageLevelData
import parking.slotsManagement.interaction.GarageLevelOption.Delete
import parking.slotsManagement.interaction.GarageLevelOption.Edit
import parking.slotsManagement.interaction.SlotsManagementScreenState.Content

class SlotsManagementUiMapper(
    val dictionary: Dictionary,
) {
    fun map(
        data: List<GarageLevelData>,
        levelCreatorVisible: Boolean,
        selectedGarageLevelData: GarageLevelData?,
        questionDialogVisible: Boolean,
    ) = Content(
        screenTitle = dictionary.getString(MR.strings.slots_management_screen_title),
        itemLevelIdentifier = dictionary.getString(MR.strings.slots_management_level_identifier),
        numberOfSpotsIdentifier = dictionary.getString(MR.strings.slots_management_number_of_spots_identifier),
        levels = data,
        availableOptions = listOf(
            Edit(
                displayedText = dictionary.getString(MR.strings.slots_management_level_option_edit),
                iconResource = ImageType.Resource(MR.images.edit_icon),
            ),
            Delete(
                displayedText = dictionary.getString(MR.strings.slots_management_level_option_delete),
                iconResource = ImageType.Resource(MR.images.delete_icon),
            ),
        ),
        leadingSpotIdentifierText = dictionary.getString(MR.strings.slots_management_leading_spot_identifier_text),
        levelCreatorVisible = levelCreatorVisible,
        selectedGarageLevel = selectedGarageLevelData,
        dialogData = QuestionDialogData(
            isVisible = questionDialogVisible,
            title = formatDialogMessage(
                dialogText = dictionary.getString(MR.strings.general_delete_question),
                selectedGarageLevelData = selectedGarageLevelData,
            ),
            question = formatDialogMessage(
                dialogText = dictionary.getString(MR.strings.garage_level_creator_delete_level_question),
                selectedGarageLevelData = selectedGarageLevelData,
            ),
            negativeActionText = dictionary.getString(MR.strings.general_cancel),
            positiveActionText = dictionary.getString(MR.strings.general_delete),
        ),
        itemMenuIconResource = ImageType.Resource(MR.images.dots),
    )

    private fun formatDialogMessage(
        dialogText: String,
        selectedGarageLevelData: GarageLevelData?,
    ) = selectedGarageLevelData?.level?.title?.let { title ->
        String.format(dialogText, title)
    } ?: dictionary.getString(MR.strings.general_unknown_error_title)
}
