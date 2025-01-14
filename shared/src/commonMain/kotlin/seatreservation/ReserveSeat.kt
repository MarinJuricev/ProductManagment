package seatreservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.fx.coroutines.parZip
import auth.Authentication
import core.utils.UUIDProvider
import seatreservation.model.SeatReservation
import seatreservation.model.SeatReservationError
import seatreservation.model.SeatReservationError.CurrentUserNotFound
import seatreservation.model.SeatReservationError.OfficeAlreadyFull
import seatreservation.repository.SeatReservationsRepository
import user.model.InventoryAppUser

class ReserveSeat(
    private val officeContainsEmptySeats: OfficeContainsEmptySeats,
    private val seatReservationsRepository: SeatReservationsRepository,
    private val authentication: Authentication,
    private val uuidProvider: UUIDProvider,
) {
    suspend operator fun invoke(
        officeId: String,
        date: Long,
    ): Either<SeatReservationError, Unit> = either {
        parZip(
            { checkForEmptySeats(officeId, date) },
            { getCurrentUser() },
        ) { _, user ->
            seatReservationsRepository.reserveSeatFor(
                seatReservation = SeatReservation(
                    id = uuidProvider.generateUUID(),
                    date = date,
                    officeId = officeId,
                    userId = user.id,
                ),
            )
        }
    }

    private suspend fun Raise<SeatReservationError>.checkForEmptySeats(
        officeId: String,
        date: Long,
    ) {
        val officeContainsEmptySeats = officeContainsEmptySeats(
            officeId = officeId,
            date = date,
        ).mapLeft { it }
            .bind()

        ensure(officeContainsEmptySeats) { OfficeAlreadyFull }
    }

    private suspend fun Raise<CurrentUserNotFound>.getCurrentUser(): InventoryAppUser = authentication
        .getCurrentUser()
        .mapLeft { CurrentUserNotFound }
        .bind()
}
