package seatreservation.model

data class SeatReservation(
    val id: String,
    val officeId: String,
    val userId: String,
    val date: Long,
) {
    fun toDto() = SeatReservationDto(
        id = id,
        officeId = officeId,
        userId = userId,
        date = date.toDouble(),
    )
}
