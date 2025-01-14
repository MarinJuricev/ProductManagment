package parking.slotsManagement.levelCreator.mapper

import parking.reservation.model.GarageLevel
import parking.reservation.model.ParkingSpot
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent
import parking.slotsManagement.levelCreator.model.LevelCreatorStepOptionState.Disabled
import parking.slotsManagement.levelCreator.model.LevelCreatorStepOptionState.Enabled
import parking.slotsManagement.levelCreator.model.LevelSpotsStep

class LevelSpotsStepUiMapper {
    fun map(
        inputFieldText: String,
        inputTextFieldError: String?,
        spots: List<ParkingSpot>,
        levelId: String,
        levelName: String,
        screenTexts: LevelSpotsStepTexts,
    ) = LevelSpotsStep(
        spotsPlaceholder = screenTexts.spotsPlaceholder,
        inputTextFieldPlaceholder = screenTexts.inputTextFieldPlaceholder,
        saveOptionText = screenTexts.saveOptionText,
        saveOptionState = if (spots.isEmpty()) Disabled else Enabled,
        previousOptionText = screenTexts.previousOptionText,
        spots = spots,
        spotLeadingIdentifier = screenTexts.spotLeadingIdentifier,
        inputTextFieldValue = inputFieldText,
        inputTextFieldError = inputTextFieldError,
        addButtonEnabled = inputFieldText.isNotBlank(),
        levelId = levelId,
        levelName = levelName,
        previousOptionState = Enabled,
        saveOptionEvent = LevelCreatorEvent.SaveClick(
            garageLevel = GarageLevel(
                id = levelId,
                title = levelName,
            ),
            spots = spots,
        ),
        previousOptionEvent = LevelCreatorEvent.PreviousStepClick,
    )
}

data class LevelSpotsStepTexts(
    val spotsPlaceholder: String,
    val inputTextFieldPlaceholder: String,
    val saveOptionText: String,
    val previousOptionText: String,
    val spotLeadingIdentifier: String,
)
