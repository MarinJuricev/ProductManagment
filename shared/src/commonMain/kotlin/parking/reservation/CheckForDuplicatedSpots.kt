package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import parking.reservation.model.GarageLevelError
import parking.reservation.model.GarageLevelError.DuplicatedSpotName
import parking.reservation.model.ParkingSpot

class CheckForDuplicatedSpots {
    operator fun invoke(spots: List<ParkingSpot>): Either<GarageLevelError, Unit> =
        either {
            val duplicatedElements = spots.groupingBy { it.title }.eachCount().filter { it.value > 1 }
            ensure(duplicatedElements.isEmpty()) { DuplicatedSpotName }
        }
}
