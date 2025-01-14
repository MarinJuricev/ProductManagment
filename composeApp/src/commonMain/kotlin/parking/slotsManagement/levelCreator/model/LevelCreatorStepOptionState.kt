package parking.slotsManagement.levelCreator.model

sealed interface LevelCreatorStepOptionState {
    data object Enabled : LevelCreatorStepOptionState
    data object Disabled : LevelCreatorStepOptionState
    data object Invisible : LevelCreatorStepOptionState
}
