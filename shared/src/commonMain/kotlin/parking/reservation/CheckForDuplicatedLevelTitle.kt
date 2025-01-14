package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelError
import parking.reservation.model.GarageLevelError.DuplicatedLevelName

class CheckForDuplicatedLevelTitle(
    private val getGarageLevels: GetGarageLevels,
) {
    suspend operator fun invoke(level: GarageLevel): Either<GarageLevelError, Unit> =
        either {
            val garageLevels = getGarageLevels
                .invoke()
                .mapLeft { GarageLevelError.StorageError }
                .bind()
            ensure(garageLevels.all { it.level.id == level.id || it.level.title != level.title }) { DuplicatedLevelName }
        }
}
