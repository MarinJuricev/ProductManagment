package seatreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class Office(
    val id: String,
    val title: String,
    val numberOfSeats: Int,
)
