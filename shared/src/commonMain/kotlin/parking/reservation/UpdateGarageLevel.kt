package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.GarageLevelData
import parking.reservation.model.GarageLevelError
import parking.reservation.repository.GarageLevelRepository

class UpdateGarageLevel(
    private val garageLevelRepository: GarageLevelRepository,
    private val authentication: Authentication,
    private val checkForDuplicatedLevelTitle: CheckForDuplicatedLevelTitle,
    private val checkForDuplicatedSpots: CheckForDuplicatedSpots,
    private val updateInvalidUserRequests: UpdateInvalidUserRequests,
    private val checkInputFieldLength: CheckInputFieldLength,
) {
    suspend operator fun invoke(garageLevelData: GarageLevelData): Either<GarageLevelError, Unit> = either {
        ensure(authentication.isUserLoggedIn()) { GarageLevelError.Unauthorized }
        checkForDuplicatedSpots(spots = garageLevelData.spots).bind()
        checkForDuplicatedLevelTitle(level = garageLevelData.level).bind()
        checkInputFieldLength(input = garageLevelData.level.title).bind()
        garageLevelData.spots.forEach {
            checkInputFieldLength(input = it.title).bind()
        }

        garageLevelRepository.updateLevelWithId(
            id = garageLevelData.id,
            level = garageLevelData.level.copy(title = garageLevelData.level.title.trim()),
            spots = garageLevelData.spots,
        )
        updateInvalidUserRequests()
    }
}
