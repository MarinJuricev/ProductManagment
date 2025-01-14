package parking.usersRequests.details.model

import parking.reservation.model.GarageLevel

sealed interface GarageLevelUi {
    data object Undefined : GarageLevelUi
    data object Deselected : GarageLevelUi
    data object Loading : GarageLevelUi
    data class GarageLevelUiModel(
        val title: String,
        val garageLevel: GarageLevel,
        val spots: List<GarageSpotUi>,
    ) : GarageLevelUi
}
