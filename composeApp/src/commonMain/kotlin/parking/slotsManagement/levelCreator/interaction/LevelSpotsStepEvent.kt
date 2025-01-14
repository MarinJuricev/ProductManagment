package parking.slotsManagement.levelCreator.interaction

import parking.reservation.model.ParkingSpot

sealed interface LevelSpotsStepEvent {
    data class TextInputChanged(val value: String) : LevelSpotsStepEvent
    data class AddClick(val spotName: String) : LevelSpotsStepEvent
    data class SpotRemoved(val spot: ParkingSpot) : LevelSpotsStepEvent
}
