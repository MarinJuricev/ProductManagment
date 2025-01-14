package parking.reservation.model

import core.utils.UUID
import kotlinx.datetime.Clock

sealed class ParkingRequest(
    open val email: String,
    open val date: Long,
) {

    fun toDto(
        id: String = UUID(),
        createdAt: Double = Clock.System.now().toEpochMilliseconds().toDouble(),
        updatedAt: Double = Clock.System.now().toEpochMilliseconds().toDouble(),
    ) = ParkingReservationDto(
        id = id,
        email = email,
        date = date.toDouble(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        note = when (this) {
            is Request -> note
            is Reservation -> ""
        },
        status = when (this) {
            is Request -> ParkingReservationStatus.Submitted
            is Reservation -> ParkingReservationStatus.Approved(adminNote = adminNote, parkingCoordinate = parkingCoordinate)
        },
    )

    data class Request(
        override val email: String,
        override val date: Long,
        val note: String,
    ) : ParkingRequest(email = email, date = date)

    data class Reservation(
        override val email: String,
        override val date: Long,
        val adminNote: String,
        val parkingCoordinate: ParkingCoordinate,
    ) : ParkingRequest(email = email, date = date)
}
