package parking.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingReservationDto(
    val id: String,
    val email: String,
    val date: Double,
    val note: String,
    val status: ParkingReservationStatus,
    val createdAt: Double,
    val updatedAt: Double,
) {
    fun toDomain() = ParkingReservation(
        id = id,
        email = email,
        date = date.toLong(),
        note = note,
        status = status,
        createdAt = createdAt.toLong(),
        updatedAt = updatedAt.toLong(),
    )
}
