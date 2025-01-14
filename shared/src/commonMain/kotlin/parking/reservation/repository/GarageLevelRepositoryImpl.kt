package parking.reservation.repository

import arrow.core.Either
import core.model.RepositoryError
import core.utils.safeDatabaseOperation
import database.Database
import database.path.GARAGE_LEVELS_PATH
import dev.gitlive.firebase.firestore.Direction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingSpot

class GarageLevelRepositoryImpl(
    private val database: Database,
) : GarageLevelRepository {
    override suspend fun createLevel(level: GarageLevelData): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database.save(
                id = level.id,
                path = GARAGE_LEVELS_PATH,
                data = level,
            )
        }

    override suspend fun updateLevelWithId(
        id: String,
        level: GarageLevel,
        spots: List<ParkingSpot>,
    ): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database.update(
                id = id,
                path = GARAGE_LEVELS_PATH,
                fields = mapOf(
                    GARAGE_LEVEL_TITLE to level,
                    GARAGE_LEVEL_SPOTS to spots,
                ),
            )
        }

    override suspend fun deleteLevelWithId(id: String): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database.deleteDocument(
                id = id,
                path = GARAGE_LEVELS_PATH,
            )
        }

    override suspend fun getLevels(): Either<RepositoryError, List<GarageLevelData>> =
        safeDatabaseOperation {
            database.getCollection(path = GARAGE_LEVELS_PATH)
                .map { it.data<GarageLevelData>() }
        }

    override suspend fun observeGarageLevels(): Flow<List<GarageLevelData>> =
        database.observeCollection(
            path = GARAGE_LEVELS_PATH,
            orderByField = LEVEL_TITLE,
            orderDirection = Direction.ASCENDING,
        ).map { list -> list.map { document -> document.data<GarageLevelData>() } }

    override suspend fun getLevelForId(id: String): Either<RepositoryError, GarageLevelData> =
        safeDatabaseOperation {
            database.getDocument(
                id = id,
                path = GARAGE_LEVELS_PATH,
            ).data<GarageLevelData>()
        }
}

private const val GARAGE_LEVEL_TITLE = "level"
private const val GARAGE_LEVEL_SPOTS = "spots"
private const val LEVEL_TITLE = "level.title"
