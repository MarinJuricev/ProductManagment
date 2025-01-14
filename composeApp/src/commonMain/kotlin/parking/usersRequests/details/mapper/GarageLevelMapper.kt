package parking.usersRequests.details.mapper

import parking.reservation.model.GarageLevelData
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi

fun GarageLevelData.toUiModel() = GarageLevelUi.GarageLevelUiModel(
    title = level.title,
    garageLevel = level,
    spots = spots.map { GarageSpotUi.GarageSpotUiModel(it) },
)
