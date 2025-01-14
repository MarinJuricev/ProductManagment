package parking.usersRequests

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import email.SendReservationUpdateEmail
import parking.reservation.model.ParkingReservation
import parking.reservation.model.toTemplateStatus
import parking.usersRequests.model.UserRequestsError
import parking.usersRequests.repository.UsersRequestsRepository

class UpdateUserRequest(
    private val repository: UsersRequestsRepository,
    private val authentication: Authentication,
    private val sendReservationUpdateEmail: SendReservationUpdateEmail,
) {
    suspend operator fun invoke(parkingReservation: ParkingReservation): Either<UserRequestsError, Unit> =
        either {
            ensure(authentication.isUserLoggedIn()) { UserRequestsError.Unauthorized }
            repository.updateUserRequest(
                parkingReservation = parkingReservation,
            )
                .onRight {
                    sendReservationUpdateEmail(
                        reservation = parkingReservation,
                        templateStatus = parkingReservation.status.toTemplateStatus(),
                    )
                }
        }
}
