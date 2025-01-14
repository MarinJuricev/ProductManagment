package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingReservationError.ErrorMessage
import parking.reservation.model.ParkingReservationError.Unauthorized
import parking.reservation.repository.GarageLevelRepository

class GetGarageLevels(
    private val garageLevelRepository: GarageLevelRepository,
    private val authentication: Authentication,
) {
    suspend operator fun invoke(): Either<ParkingReservationError, List<GarageLevelData>> = either {
        ensure(authentication.isUserLoggedIn()) { Unauthorized }
        garageLevelRepository
            .getLevels()
            .mapLeft { ErrorMessage("Failed to fetch") }
            .bind()
    }
}
