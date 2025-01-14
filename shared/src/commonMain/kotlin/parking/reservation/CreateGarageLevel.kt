package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.reservation.model.GarageLevelData
import parking.reservation.model.GarageLevelError
import parking.reservation.model.GarageLevelRequest
import parking.reservation.repository.GarageLevelRepository

class CreateGarageLevel(
    private val garageLevelRepository: GarageLevelRepository,
    private val authentication: Authentication,
    private val checkForDuplicatedLevelTitle: CheckForDuplicatedLevelTitle,
    private val checkForDuplicatedSpots: CheckForDuplicatedSpots,
    private val checkInputFieldLength: CheckInputFieldLength,
) {
    suspend operator fun invoke(request: GarageLevelRequest): Either<GarageLevelError, GarageLevelData> = either {
        ensure(authentication.isUserLoggedIn()) { GarageLevelError.Unauthorized }
        checkForDuplicatedSpots(spots = request.spots).bind()
        checkForDuplicatedLevelTitle(level = request.garageLevel).bind()
        checkInputFieldLength(request.garageLevel.title).bind()
        request.spots.forEach {
            checkInputFieldLength(input = it.title).bind()
        }

        request.toDto().also { garageLevelRepository.createLevel(it) }
    }
}
