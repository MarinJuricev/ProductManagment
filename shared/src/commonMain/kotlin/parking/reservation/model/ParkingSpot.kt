package parking.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingSpot(
    val id: String,
    val title: String,
)
