package parking.reservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.ParkingRequest.Reservation
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingReservationError.Unauthorized
import parking.reservation.repository.ParkingReservationRepository

class MakeReservation(
    private val repository: ParkingReservationRepository,
    private val authentication: Authentication,
    private val isUserAbleToMakeReservation: IsUserAbleToMakeReservation,
) {
    suspend operator fun invoke(reservation: Reservation): Either<ParkingReservationError, Unit> =
        either {
            checkIfAuthorized()

            authentication.getCurrentUser()
            repository.createReservation(reservation)
        }

    private suspend fun Raise<Unauthorized>.checkIfAuthorized() {
        val user = authentication.getCurrentUser()
            .mapLeft { Unauthorized }
            .bind()

        ensure(isUserAbleToMakeReservation(user)) { Unauthorized }
    }
}
