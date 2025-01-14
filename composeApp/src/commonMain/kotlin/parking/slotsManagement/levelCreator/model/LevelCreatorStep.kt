package parking.slotsManagement.levelCreator.model

import parking.reservation.model.ParkingSpot
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent

sealed class LevelCreatorStep(
    val firstOptionName: String,
    val firstOptionState: LevelCreatorStepOptionState,
    val firstOptionEvent: LevelCreatorEvent?,
    val secondOptionName: String,
    val secondOptionState: LevelCreatorStepOptionState,
    val secondOptionEvent: LevelCreatorEvent?,
)

data class LevelNameStep(
    val levelNameFormTitle: String,
    val levelId: String,
    val levelName: String,
    val levelNameError: String?,
    val proceedOptionText: String,
    val proceedOptionState: LevelCreatorStepOptionState,
    val levelNamePlaceholder: String,
    val previousOptionName: String,
    val previousOptionState: LevelCreatorStepOptionState,
    val continueOptionEvent: LevelCreatorEvent,
    val previousOptionEvent: LevelCreatorEvent?,
) : LevelCreatorStep(
    firstOptionName = previousOptionName,
    firstOptionState = previousOptionState,
    secondOptionName = proceedOptionText,
    secondOptionState = proceedOptionState,
    secondOptionEvent = continueOptionEvent,
    firstOptionEvent = previousOptionEvent,
)

data class LevelSpotsStep(
    val levelId: String,
    val levelName: String,
    val spotsPlaceholder: String,
    val inputTextFieldPlaceholder: String,
    val inputTextFieldValue: String,
    val inputTextFieldError: String?,
    val addButtonEnabled: Boolean,
    val saveOptionText: String,
    val saveOptionState: LevelCreatorStepOptionState,
    val previousOptionText: String,
    val spotLeadingIdentifier: String,
    val spots: List<ParkingSpot>,
    val previousOptionState: LevelCreatorStepOptionState,
    val saveOptionEvent: LevelCreatorEvent,
    val previousOptionEvent: LevelCreatorEvent,
) : LevelCreatorStep(
    firstOptionName = previousOptionText,
    firstOptionState = previousOptionState,
    secondOptionName = saveOptionText,
    secondOptionState = saveOptionState,
    secondOptionEvent = saveOptionEvent,
    firstOptionEvent = previousOptionEvent,
)
