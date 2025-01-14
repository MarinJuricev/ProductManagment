package parking.usersRequests.details.model

import parking.reservation.model.ParkingSpot

sealed interface GarageSpotUi {
    data object Undefined : GarageSpotUi
    data object Deselected : GarageSpotUi

    data class GarageSpotUiModel(
        val spot: ParkingSpot,
    ) : GarageSpotUi
}
