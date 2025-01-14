package seatreservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import auth.Authentication
import seatreservation.model.SeatReservation
import seatreservation.model.SeatReservationError
import seatreservation.model.SeatReservationError.CancelationFailed
import seatreservation.model.SeatReservationError.ReservationNotFound
import seatreservation.model.SeatReservationError.Unauthorized
import seatreservation.repository.SeatReservationsRepository
import user.model.InventoryAppUser

class CancelSeatReservation(
    private val seatReservationsRepository: SeatReservationsRepository,
    private val authentication: Authentication,
) {
    suspend operator fun invoke(
        officeId: String,
        date: Long,
    ): Either<SeatReservationError, Unit> = either {
        val user = getUser()

        val seatReservation = getSeatReservation(
            officeId = officeId,
            date = date,
            userId = user.id,
        )

        seatReservationsRepository
            .cancelSeatReservationFor(id = seatReservation.id)
            .mapLeft { CancelationFailed }
            .bind()
    }

    private suspend fun Raise<Unauthorized>.getUser(): InventoryAppUser =
        authentication
            .getCurrentUser()
            .mapLeft { Unauthorized }
            .bind()

    private suspend fun Raise<ReservationNotFound>.getSeatReservation(
        officeId: String,
        date: Long,
        userId: String,
    ): SeatReservation =
        seatReservationsRepository
            .getSeatReservation(
                officeId = officeId,
                date = date,
                userId = userId,
            )
            .mapLeft { ReservationNotFound }
            .bind()
}
