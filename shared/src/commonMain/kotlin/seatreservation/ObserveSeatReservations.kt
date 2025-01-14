package seatreservation

import kotlinx.coroutines.flow.Flow
import seatreservation.model.SeatReservation
import seatreservation.repository.SeatReservationsRepository

class ObserveSeatReservations(
    private val seatReservationsRepository: SeatReservationsRepository,
) {
    operator fun invoke(
        officeId: String,
        fromDate: Long,
        toDate: Long,
    ): Flow<List<SeatReservation>> = seatReservationsRepository
        .observeSeatReservationsFor(
            officeId = officeId,
            fromDate = fromDate,
            toDate = toDate,
        )
}
