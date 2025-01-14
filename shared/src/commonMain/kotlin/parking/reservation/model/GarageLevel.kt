package parking.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class GarageLevel(
    val id: String,
    val title: String,
)
