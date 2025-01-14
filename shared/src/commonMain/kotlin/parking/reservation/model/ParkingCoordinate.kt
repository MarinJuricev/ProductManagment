package parking.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingCoordinate(
    val level: GarageLevel,
    val spot: ParkingSpot,
)
