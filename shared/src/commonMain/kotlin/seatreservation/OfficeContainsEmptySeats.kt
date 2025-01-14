package seatreservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.fx.coroutines.parZip
import seatreservation.model.Office
import seatreservation.model.SeatReservation
import seatreservation.model.SeatReservationError
import seatreservation.model.SeatReservationError.OfficeNotFound
import seatreservation.model.SeatReservationError.SeatReservationFetchFailed
import seatreservation.repository.OfficeRepository
import seatreservation.repository.SeatReservationsRepository

class OfficeContainsEmptySeats(
    private val seatReservationsRepository: SeatReservationsRepository,
    private val officeRepository: OfficeRepository,

) {
    suspend operator fun invoke(
        officeId: String,
        date: Long,
    ): Either<SeatReservationError, Boolean> = either {
        parZip(
            {
                getSeatReservationsFor(
                    officeId = officeId,
                    date = date,
                )
            },
            { getOfficeFor(id = officeId) },
        ) { seatReservations, office ->
            seatReservations.size < office.numberOfSeats
        }
    }

    private suspend fun Raise<SeatReservationError>.getSeatReservationsFor(
        officeId: String,
        date: Long,
    ): List<SeatReservation> =
        seatReservationsRepository.getSeatReservationsFor(
            officeId = officeId,
            date = date,
        )
            .mapLeft { SeatReservationFetchFailed }
            .bind()

    private suspend fun Raise<SeatReservationError>.getOfficeFor(id: String): Office =
        officeRepository.getOfficeFor(id = id)
            .mapLeft { OfficeNotFound }
            .bind()
}
