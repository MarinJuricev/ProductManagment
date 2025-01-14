package parking.slotsManagement.levelCreator.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent
import parking.slotsManagement.levelCreator.model.LevelCreatorStepOptionState
import parking.slotsManagement.levelCreator.model.LevelCreatorStepOptionState.Disabled
import parking.slotsManagement.levelCreator.model.LevelCreatorStepOptionState.Enabled
import parking.slotsManagement.levelCreator.model.LevelNameStep

class LevelNameStepUiMapper(
    private val dictionary: Dictionary,
) {
    fun map(
        levelName: String? = null,
        levelNameError: String?,
        levelId: String,
        screenTexts: LevelNameStepTexts,
    ): LevelNameStep {
        val selectedName = levelName.orEmpty()
        val currentLevelNameError = checkForNamingErrors(levelNameError, selectedName)
        return LevelNameStep(
            levelNameFormTitle = screenTexts.levelNameFormTitle,
            levelName = selectedName,
            levelNameError = currentLevelNameError,
            proceedOptionText = screenTexts.proceedOptionText,
            proceedOptionState = if (selectedName.isNotBlank() && currentLevelNameError == null) Enabled else Disabled,
            levelNamePlaceholder = screenTexts.levelNamePlaceholder,
            levelId = levelId,
            previousOptionName = "",
            previousOptionState = LevelCreatorStepOptionState.Invisible,
            continueOptionEvent = LevelCreatorEvent.NextStepClick,
            previousOptionEvent = null,
        )
    }

    private fun checkForNamingErrors(
        levelNameError: String?,
        selectedName: String,
    ): String? = when {
        levelNameError?.isNotBlank() == true -> levelNameError
        selectedName.length > MAX_NAME_LENGTH -> dictionary.getString(MR.strings.slots_management_parking_name_length_error)
        else -> null
    }
}

data class LevelNameStepTexts(
    val levelNameFormTitle: String,
    val proceedOptionText: String,
    val levelNamePlaceholder: String,
)

private const val MAX_NAME_LENGTH = 20
