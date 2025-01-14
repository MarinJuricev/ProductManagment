package parking.slotsManagement.levelCreator.interaction

import parking.slotsManagement.levelCreator.model.LevelCreatorStep

sealed interface LevelCreatorUiState {
    data class Content(
        val screenTitle: String,
        val stepIdentifier: String,
        val currentStepIndex: Int,
        val currentStepText: String,
        val lastStepText: String,
        val stepSeparator: String,
        val availableSteps: List<LevelCreatorStep>,
        val currentStep: LevelCreatorStep,
    ) : LevelCreatorUiState

    data object Loading : LevelCreatorUiState
}
