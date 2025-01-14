package parking.reservation.validator

import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot

interface GarageLevelValidator {
    fun validateGarageLevelTitleCreation(
        unavailableTitles: List<String>,
        title: String,
    ): String?
    fun validateGarageLevelTitleEdit(
        initialGarageLevel: GarageLevel,
        unavailableGarageLevels: List<GarageLevel>,
        title: String,
    ): String?
    fun validateParkingSpotTitle(
        unavailableTitles: List<String>,
        title: String,
    ): String?
    fun isGarageLevelCreationValid(
        unavailableTitles: List<String>,
        title: String,
        spots: List<ParkingSpot>,
    ): Boolean
    fun isGarageLevelEditingValid(
        initialGarageLevelData: GarageLevelData,
        unavailableGarageLevels: List<GarageLevel>,
        title: String,
        spots: List<ParkingSpot>,
    ): Boolean
    fun isParkingSpotCreationValid(
        unavailableTitles: List<String>,
        title: String,
    ): Boolean
}
