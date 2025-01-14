package parking.usersRequests

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.ParkingReservation
import parking.usersRequests.model.UserRequestsError
import parking.usersRequests.model.UserRequestsError.Unauthorized
import parking.usersRequests.model.UserRequestsError.UnknownError
import parking.usersRequests.repository.UsersRequestsRepository

class GetUsersRequests(
    private val repository: UsersRequestsRepository,
    private val authentication: Authentication,
) {
    suspend operator fun invoke(
        startDate: Long,
        endDate: Long,
    ): Either<UserRequestsError, List<ParkingReservation>> = either {
        ensure(authentication.isUserLoggedIn()) { Unauthorized }
        repository.getUsersRequests(startDate = startDate, endDate = endDate)
            .mapLeft { UnknownError }.bind()
    }
}
