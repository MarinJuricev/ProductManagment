package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import core.model.DatabaseOperationError
import core.model.NetworkError
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationDto
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingReservationError.ErrorMessage
import parking.reservation.model.ParkingReservationError.Unauthorized
import parking.reservation.repository.ParkingReservationRepository

class MyParkingRequests(
    private val repository: ParkingReservationRepository,
    private val authentication: Authentication,
) {
    suspend operator fun invoke(
        startDate: Long,
        endDate: Long,
    ): Either<ParkingReservationError, List<ParkingReservation>> = either {
        ensure(authentication.isUserLoggedIn()) { Unauthorized }
        val user = authentication.getCurrentUser().mapLeft { Unauthorized }.bind()

        repository.getReservationsForUser(
            email = user.email,
            startDate = startDate,
            endDate = endDate,
        ).map { list -> list.map(ParkingReservationDto::toDomain) }.mapLeft { repositoryError ->
            when (repositoryError) {
                is DatabaseOperationError.UnknownError -> ErrorMessage(repositoryError.message)
                is NetworkError.BackendError -> ErrorMessage(repositoryError.errorMessage.orEmpty())
                is NetworkError.UnknownNetworkError -> ParkingReservationError.UnknownError
            }
        }.bind()
    }
}
