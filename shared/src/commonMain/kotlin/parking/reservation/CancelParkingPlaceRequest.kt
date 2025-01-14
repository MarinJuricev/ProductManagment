package parking.reservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingReservationError.Unauthorized
import parking.reservation.model.ParkingReservationStatus.Canceled
import parking.usersRequests.repository.UsersRequestsRepository
import user.model.InventoryAppRole.Administrator
import user.model.InventoryAppUser

class CancelParkingPlaceRequest(
    private val repository: UsersRequestsRepository,
    private val authentication: Authentication,
) {
    suspend operator fun invoke(parkingReservation: ParkingReservation): Either<ParkingReservationError, Unit> =
        either {
            val user = getUser()
            val updatedParkingReservation = parkingReservation.copy(status = Canceled)
            ensure(parkingReservation.email == user.email || user.role is Administrator) { Unauthorized }
            repository.updateUserRequest(parkingReservation = updatedParkingReservation)
        }

    private suspend fun Raise<ParkingReservationError>.getUser(): InventoryAppUser =
        authentication
            .getCurrentUser()
            .mapLeft { Unauthorized }
            .bind()
}
