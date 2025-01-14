package parking.slotsManagement.levelCreator.mapper

import core.utils.UUIDProvider
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot
import parking.slotsManagement.levelCreator.interaction.LevelCreatorUiState.Content

class LevelCreatorUiMapper(
    private val levelNameStepUiMapper: LevelNameStepUiMapper,
    private val levelSpotsStepUiMapper: LevelSpotsStepUiMapper,
    private val uuidProvider: UUIDProvider,
    private val dictionary: Dictionary,
) {

    fun map(
        editableFields: LevelCreatorEditableFields,
        spots: List<ParkingSpot>? = null,
        screenTexts: LevelCreatorContentTexts,
    ): Content {
        val selectedLevelName: String =
            editableFields.levelName ?: editableFields.selectedGarageLevel?.level?.title.orEmpty()
        val selectedId =
            editableFields.selectedGarageLevel?.level?.id ?: uuidProvider.generateUUID()
        val selectedSpots = spots ?: editableFields.selectedGarageLevel?.spots ?: listOf()
        val levelNameError =
            if (editableFields.existingGarageLevels.any { it.level.id != selectedId && it.level.title == selectedLevelName.trim() }) {
                dictionary.getString(MR.strings.slots_management_duplicated_level_error)
            } else {
                null
            }
        val availableSteps = listOf(
            levelNameStepUiMapper.map(
                levelName = selectedLevelName,
                levelId = selectedId,
                screenTexts = screenTexts.levelNameStepTexts,
                levelNameError = levelNameError,
            ),
            levelSpotsStepUiMapper.map(
                inputFieldText = editableFields.spotName.orEmpty(),
                inputTextFieldError = editableFields.spotNameError,
                spots = selectedSpots,
                levelId = selectedId,
                levelName = selectedLevelName,
                screenTexts = screenTexts.levelSpotsStepTexts,
            ),
        )
        val selectedStepIndex = editableFields.currentStep.coerceIn(0, availableSteps.lastIndex)
        val currentStep = availableSteps[selectedStepIndex]
        return Content(
            screenTitle = screenTexts.screenTitle,
            stepIdentifier = screenTexts.stepIdentifier,
            currentStepIndex = selectedStepIndex,
            currentStepText = (selectedStepIndex + 1).toString(),
            lastStepText = availableSteps.size.toString(),
            stepSeparator = screenTexts.stepSeparator,
            availableSteps = availableSteps,
            currentStep = currentStep,
        )
    }
}

data class LevelCreatorContentTexts(
    val screenTitle: String,
    val stepIdentifier: String,
    val stepSeparator: String,
    val levelNameStepTexts: LevelNameStepTexts,
    val levelSpotsStepTexts: LevelSpotsStepTexts,
)

data class LevelCreatorEditableFields(
    val spotName: String? = null,
    val levelName: String? = null,
    val selectedGarageLevel: GarageLevelData? = null,
    val existingGarageLevels: List<GarageLevelData> = listOf(),
    val currentStep: Int = 0,
    val spotNameError: String? = null,
    val levelNameError: String? = null,
)
