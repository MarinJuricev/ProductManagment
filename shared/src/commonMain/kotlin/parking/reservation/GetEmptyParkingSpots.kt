package parking.reservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.fx.coroutines.parZip
import auth.Authentication
import parking.reservation.model.GarageLevelData
import parking.reservation.model.GarageLevelError
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.repository.GarageLevelRepository
import parking.reservation.repository.ParkingReservationRepository

class GetEmptyParkingSpots(
    private val parkingReservationRepository: ParkingReservationRepository,
    private val garageLevelRepository: GarageLevelRepository,
    private val authentication: Authentication,
) {

    suspend operator fun invoke(
        date: Long,
        userRequestId: String?,
    ): Either<GarageLevelError, List<GarageLevelData>> = either {
        ensure(authentication.isUserLoggedIn()) { GarageLevelError.Unauthorized }
        parZip(
            { getOccupiedParkingCoordinates(date = date) },
            { getUserRequestParkingCoordinate(id = userRequestId) },
        ) { occupiedParkingCoordinates, userRequestParkingCoordinate ->
            garageLevelRepository
                .getLevels()
                .mapLeft { GarageLevelError.StorageError }
                .bind()
                .filterOut(occupiedParkingCoordinates = occupiedParkingCoordinates)
                .addIfNeeded(userRequestParkingCoordinate = userRequestParkingCoordinate)
                .filterNot { it.spots.isEmpty() }
        }
    }

    private suspend fun Raise<GarageLevelError>.getOccupiedParkingCoordinates(date: Long): List<ParkingCoordinate> =
        parkingReservationRepository
            .getReservationsForDate(date = date)
            .mapLeft { GarageLevelError.ParkingReservationStorageError }
            .bind()
            .mapNotNull {
                when (it.status) {
                    is ParkingReservationStatus.Approved -> it.status.parkingCoordinate
                    else -> null
                }
            }

    private suspend fun Raise<GarageLevelError>.getUserRequestParkingCoordinate(id: String?): ParkingCoordinate? {
        if (id == null) return null

        val userRequest = parkingReservationRepository
            .getReservationForId(id = id)
            .mapLeft { GarageLevelError.ParkingReservationStorageError }
            .bind()
            .status

        return when (userRequest) {
            is ParkingReservationStatus.Approved -> userRequest.parkingCoordinate
            else -> null
        }
    }

    private fun List<GarageLevelData>.filterOut(occupiedParkingCoordinates: List<ParkingCoordinate>): List<GarageLevelData> {
        if (occupiedParkingCoordinates.isEmpty()) return this

        return this.map { levelData ->
            val occupiedSpots = occupiedParkingCoordinates.mapNotNull { occupiedParkingCoordinate ->
                if (occupiedParkingCoordinate.level.id != levelData.level.id) return@mapNotNull null

                return@mapNotNull levelData.spots.filter { garageSpot ->
                    occupiedParkingCoordinate.spot.id == garageSpot.id
                }
            }
                .flatten()

            levelData.copy(
                spots = levelData.spots.filterNot { spot -> spot in occupiedSpots },
            )
        }
    }

    private fun List<GarageLevelData>.addIfNeeded(userRequestParkingCoordinate: ParkingCoordinate?): List<GarageLevelData> {
        if (userRequestParkingCoordinate == null) return this

        return map { levelData ->
            levelData.copy(
                spots = if (levelData.level.id != userRequestParkingCoordinate.level.id) {
                    levelData.spots
                } else {
                    levelData.spots.plus(userRequestParkingCoordinate.spot)
                },
            )
        }
    }
}
