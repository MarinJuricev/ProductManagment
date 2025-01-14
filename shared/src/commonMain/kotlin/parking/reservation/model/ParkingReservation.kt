package parking.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingReservation(
    val id: String,
    val email: String,
    val date: Long,
    val note: String,
    val status: ParkingReservationStatus,
    val createdAt: Long,
    val updatedAt: Long,
) {
    fun toDto() = ParkingReservationDto(
        id = id,
        email = email,
        date = date.toDouble(),
        note = note,
        status = status,
        createdAt = createdAt.toDouble(),
        updatedAt = updatedAt.toDouble(),
    )
}
