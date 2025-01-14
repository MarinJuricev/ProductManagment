package parking.slotsManagement.levelCreator.interaction

import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot

sealed interface LevelCreatorEvent {
    data class LevelCreatorShown(val selectedGarageLevel: GarageLevelData? = null, val existingGarageLevels: List<GarageLevelData>) : LevelCreatorEvent
    data class LevelNameChange(val newName: String) : LevelCreatorEvent
    data class SpotNameChange(val newName: String) : LevelCreatorEvent
    data class SpotRemoved(val spotId: String) : LevelCreatorEvent
    data class SpotCreated(val spotName: String) : LevelCreatorEvent
    data object NextStepClick : LevelCreatorEvent
    data object PreviousStepClick : LevelCreatorEvent
    data class SaveClick(
        val garageLevel: GarageLevel,
        val spots: List<ParkingSpot>,
    ) : LevelCreatorEvent
}
