package parking.usersRequests.repository

import arrow.core.Either
import core.model.RepositoryError
import core.utils.endOfTheDay
import core.utils.safeDatabaseOperation
import core.utils.startOfTheDay
import database.Database
import database.path.PARKING_REQUEST_PATH
import dev.gitlive.firebase.firestore.Direction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationDto

internal class UsersRequestsRepositoryImpl(private val database: Database) : UsersRequestsRepository {
    override suspend fun getUsersRequests(
        startDate: Long,
        endDate: Long,
    ): Either<RepositoryError, List<ParkingReservation>> = safeDatabaseOperation {
        val startOfTheDay = startDate.startOfTheDay()
        val endOfTheDay = endDate.endOfTheDay()
        database.queryCollection(
            path = PARKING_REQUEST_PATH,
            query = {
                RESERVATION_DATE greaterThanOrEqualTo startOfTheDay and (RESERVATION_DATE lessThanOrEqualTo endOfTheDay)
            },
        ).map {
            it.data<ParkingReservationDto>().toDomain()
        }
    }

    override suspend fun getApprovedUsersRequests(): Either<RepositoryError, List<ParkingReservation>> = safeDatabaseOperation {
        database.queryCollection(
            path = PARKING_REQUEST_PATH,
            query = { RESERVATION_STATUS_TYPE equalTo APPROVED_STATUS },
        )
            .map { it.data<ParkingReservationDto>().toDomain() }
    }

    override fun observeUserRequests(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ParkingReservation>> {
        val startOfTheDay = startDate.startOfTheDay()
        val endOfTheDay = endDate.endOfTheDay()
        return database.observeCollection(
            path = PARKING_REQUEST_PATH,
            orderByField = CREATED_AT,
            orderDirection = Direction.DESCENDING,
            query = {
                RESERVATION_DATE greaterThanOrEqualTo startOfTheDay and (RESERVATION_DATE lessThanOrEqualTo endOfTheDay)
            },
        ).map { list -> list.map { document -> document.data<ParkingReservationDto>().toDomain() } }
    }

    override suspend fun updateUserRequest(parkingReservation: ParkingReservation): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database.save(
                id = parkingReservation.id,
                path = PARKING_REQUEST_PATH,
                data = parkingReservation.toDto(),
            )
        }
}

private const val RESERVATION_DATE = "date"
private const val CREATED_AT = "createdAt"
private const val RESERVATION_STATUS_TYPE = "status.type"
private const val APPROVED_STATUS = "Approved"
