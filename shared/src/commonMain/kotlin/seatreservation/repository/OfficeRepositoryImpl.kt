package seatreservation.repository

import arrow.core.Either
import core.model.RepositoryError
import core.utils.safeDatabaseOperation
import database.Database
import database.path.SEAT_OFFICES_PATH
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import seatreservation.model.Office

internal class OfficeRepositoryImpl(
    private val database: Database,
) : OfficeRepository {
    override suspend fun getOffices(): Either<RepositoryError, List<Office>> =
        safeDatabaseOperation {
            database.getCollection(
                path = SEAT_OFFICES_PATH,
            ).map { it.data<Office>() }
        }

    override suspend fun saveOffice(office: Office): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database.save(
                id = office.id,
                path = SEAT_OFFICES_PATH,
                data = office,
            )
        }

    override suspend fun getOfficeFor(id: String): Either<RepositoryError, Office> =
        safeDatabaseOperation {
            database.getDocument(
                id = id,
                path = SEAT_OFFICES_PATH,
            ).data<Office>()
        }

    override fun observeOffice(id: String): Flow<Office> = database.observeDocument(
        id = id,
        path = SEAT_OFFICES_PATH,
    ).map { it.data<Office>() }

    override fun observeOffices(): Flow<List<Office>> = database.observeCollection(
        path = SEAT_OFFICES_PATH,
    ).map { list -> list.map { it.data<Office>() } }

    override suspend fun deleteOffice(office: Office): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database.deleteDocument(
                id = office.id,
                path = SEAT_OFFICES_PATH,
            )
        }
}
