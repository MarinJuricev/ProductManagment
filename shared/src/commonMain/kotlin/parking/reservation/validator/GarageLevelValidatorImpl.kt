package parking.reservation.validator

import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot

class GarageLevelValidatorImpl : GarageLevelValidator {
    override fun validateGarageLevelTitleCreation(
        unavailableTitles: List<String>,
        title: String,
    ): String? {
        val trimmedTitle = title.trim()
        if (unavailableTitles.contains(trimmedTitle)) {
            return "This level already exists"
        }
        return validateGarageLevelTitleLength(title = trimmedTitle)
    }

    override fun validateGarageLevelTitleEdit(
        initialGarageLevel: GarageLevel,
        unavailableGarageLevels: List<GarageLevel>,
        title: String,
    ): String? {
        val trimmedTitle = title.trim()
        if (unavailableGarageLevels.any { initialGarageLevel.id != it.id && it.title == trimmedTitle }) {
            return "This level already exists"
        }
        return validateGarageLevelTitleLength(title = trimmedTitle)
    }

    override fun validateParkingSpotTitle(
        unavailableTitles: List<String>,
        title: String,
    ): String? {
        val trimmedTitle = title.trim()
        if (unavailableTitles.contains(trimmedTitle)) {
            return "This spot already exists"
        }
        return validateParkingSpotTitleLength(title = trimmedTitle)
    }

    override fun isGarageLevelCreationValid(
        unavailableTitles: List<String>,
        title: String,
        spots: List<ParkingSpot>,
    ): Boolean {
        val isGarageLevelTitleValid = validateGarageLevelTitleCreation(
            unavailableTitles = unavailableTitles,
            title = title,
        ).isNullOrBlank()
        val areSpotsValid = spots.isNotEmpty()
        return isGarageLevelTitleValid && areSpotsValid
    }

    override fun isGarageLevelEditingValid(
        initialGarageLevelData: GarageLevelData,
        unavailableGarageLevels: List<GarageLevel>,
        title: String,
        spots: List<ParkingSpot>,
    ): Boolean {
        val trimmedTitle = title.trim()
        if (initialGarageLevelData.level.title == trimmedTitle && initialGarageLevelData.spots == spots) {
            return false
        }
        val isGarageLevelTitleValid = validateGarageLevelTitleEdit(
            initialGarageLevel = initialGarageLevelData.level,
            unavailableGarageLevels = unavailableGarageLevels,
            title = title,
        ).isNullOrBlank()
        val areSpotsValid = spots.isNotEmpty()
        return isGarageLevelTitleValid && areSpotsValid
    }

    override fun isParkingSpotCreationValid(
        unavailableTitles: List<String>,
        title: String,
    ): Boolean = validateParkingSpotTitle(
        unavailableTitles = unavailableTitles,
        title = title,
    ).isNullOrBlank()

    private fun validateGarageLevelTitleLength(title: String): String? = when {
        title.isEmpty() -> "Title should not be empty"
        title.length > MAX_TITLE_LENGTH -> "Title should not exceed $MAX_TITLE_LENGTH characters"
        else -> null
    }

    private fun validateParkingSpotTitleLength(title: String): String? = when {
        title.isEmpty() -> "Title should not be empty"
        title.length > MAX_TITLE_LENGTH -> "Title should not exceed $MAX_TITLE_LENGTH characters"
        else -> null
    }
}

private const val MAX_TITLE_LENGTH = 10
