package parking.slotsManagement.levelCreator.interaction

sealed interface LevelCreatorViewEffect {
    data object DismissRequested : LevelCreatorViewEffect
}
