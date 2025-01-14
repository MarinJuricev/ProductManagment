package parking.slotsManagement.levelCreator.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class LevelCreatorTextsMapper(
    private val dictionary: Dictionary,
) {
    fun map() = LevelCreatorContentTexts(
        screenTitle = dictionary.getString(MR.strings.garage_level_creator_screen_title),
        stepIdentifier = dictionary.getString(MR.strings.garage_level_creator_step_identifier),
        stepSeparator = dictionary.getString(MR.strings.garage_level_creator_step_separator),
        levelNameStepTexts = LevelNameStepTexts(
            levelNameFormTitle = dictionary.getString(MR.strings.garage_level_creator_level_name_form_title),
            proceedOptionText = dictionary.getString(MR.strings.general_continue),
            levelNamePlaceholder = dictionary.getString(MR.strings.garage_level_creator_level_name_placeholder),
        ),
        levelSpotsStepTexts = LevelSpotsStepTexts(
            spotsPlaceholder = dictionary.getString(MR.strings.garage_level_creator_spots_placeholder),
            inputTextFieldPlaceholder = dictionary.getString(MR.strings.garage_level_creator_spot_name_placeholder),
            saveOptionText = dictionary.getString(MR.strings.general_save),
            previousOptionText = dictionary.getString(MR.strings.general_previous),
            spotLeadingIdentifier = dictionary.getString(MR.strings.garage_level_creator_spot_leading_identifier),
        ),
    )
}
