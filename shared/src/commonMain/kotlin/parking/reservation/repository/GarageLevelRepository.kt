package parking.reservation.repository

import arrow.core.Either
import core.model.RepositoryError
import kotlinx.coroutines.flow.Flow
import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot

interface GarageLevelRepository {
    suspend fun createLevel(level: GarageLevelData): Either<RepositoryError, Unit>
    suspend fun updateLevelWithId(
        id: String,
        level: GarageLevel,
        spots: List<ParkingSpot>,
    ): Either<RepositoryError, Unit>

    suspend fun deleteLevelWithId(id: String): Either<RepositoryError, Unit>
    suspend fun getLevels(): Either<RepositoryError, List<GarageLevelData>>
    suspend fun observeGarageLevels(): Flow<List<GarageLevelData>>
    suspend fun getLevelForId(id: String): Either<RepositoryError, GarageLevelData>
}
