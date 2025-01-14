package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.GarageLevelError
import parking.reservation.repository.GarageLevelRepository

class DeleteGarageLevel(
    private val garageLevelRepository: GarageLevelRepository,
    private val authentication: Authentication,
    private val updateInvalidUserRequests: UpdateInvalidUserRequests,
) {
    suspend operator fun invoke(id: String): Either<GarageLevelError, Unit> = either {
        ensure(authentication.isUserLoggedIn()) { GarageLevelError.Unauthorized }
        garageLevelRepository.deleteLevelWithId(id = id)
        updateInvalidUserRequests()
    }
}
