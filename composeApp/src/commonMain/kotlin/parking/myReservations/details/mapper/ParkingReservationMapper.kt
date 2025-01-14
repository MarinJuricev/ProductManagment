package parking.myReservations.details.mapper

import kotlinx.datetime.Clock
import parking.myReservations.details.interaction.MyReservationDetailsUiState
import parking.reservation.model.ParkingReservation

class ParkingReservationMapper {
    fun map(data: MyReservationDetailsUiState.Content) = ParkingReservation(
        id = data.id,
        email = data.email,
        date = data.dateInfo.dateTimestamp,
        note = data.adminNotes.notesForAdmin,
        status = data.reservationStatus.status,
        createdAt = data.createdAt,
        updatedAt = Clock.System.now().toEpochMilliseconds(),
    )
}
