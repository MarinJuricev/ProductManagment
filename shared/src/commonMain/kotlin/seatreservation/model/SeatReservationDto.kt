package seatreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class SeatReservationDto(
    val id: String,
    val officeId: String,
    val userId: String,
    val date: Double,
) {
    fun toDomain() = SeatReservation(
        id = id,
        officeId = officeId,
        userId = userId,
        date = date.toLong(),
    )
}
