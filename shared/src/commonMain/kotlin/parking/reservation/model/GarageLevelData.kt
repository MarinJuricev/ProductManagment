package parking.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class GarageLevelData(
    val id: String,
    val level: GarageLevel,
    val spots: List<ParkingSpot>,
)
