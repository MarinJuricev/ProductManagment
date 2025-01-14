package seatreservation.repository

import arrow.core.Either
import core.model.RepositoryError
import kotlinx.coroutines.flow.Flow
import seatreservation.model.Office

interface OfficeRepository {

    suspend fun getOffices(): Either<RepositoryError, List<Office>>
    suspend fun saveOffice(office: Office): Either<RepositoryError, Unit>
    suspend fun getOfficeFor(
        id: String,
    ): Either<RepositoryError, Office>

    fun observeOffice(
        id: String,
    ): Flow<Office>

    fun observeOffices(): Flow<List<Office>>
    suspend fun deleteOffice(office: Office): Either<RepositoryError, Unit>
}
