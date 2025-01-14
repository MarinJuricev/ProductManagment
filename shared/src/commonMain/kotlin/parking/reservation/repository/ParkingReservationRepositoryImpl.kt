package parking.reservation.repository

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
import parking.reservation.model.ParkingRequest
import parking.reservation.model.ParkingRequest.Request
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationDto

class ParkingReservationRepositoryImpl(
    private val database: Database,
) : ParkingReservationRepository {
    override suspend fun createRequest(request: Request): Either<RepositoryError, Unit> {
        val parkingReservationDto = request.toDto()
        return safeDatabaseOperation {
            database.save(
                id = parkingReservationDto.id,
                path = PARKING_REQUEST_PATH,
                data = parkingReservationDto,
            )
        }
    }

    override suspend fun createReservation(reservation: ParkingRequest.Reservation): Either<RepositoryError, ParkingReservationDto> {
        val parkingReservationDto = reservation.toDto()
        return safeDatabaseOperation {
            database.save(
                id = parkingReservationDto.id,
                path = PARKING_REQUEST_PATH,
                data = parkingReservationDto,
            )
            parkingReservationDto
        }
    }

    override suspend fun getReservationForId(id: String): Either<RepositoryError, ParkingReservationDto> =
        safeDatabaseOperation {
            database.getDocument(
                id = id,
                path = PARKING_REQUEST_PATH,
            ).data<ParkingReservationDto>()
        }

    override suspend fun getReservationsForUser(
        email: String,
        startDate: Long,
        endDate: Long,
    ): Either<RepositoryError, List<ParkingReservationDto>> =
        safeDatabaseOperation {
            val startOfTheDay = startDate.startOfTheDay()
            val endOfTheDay = endDate.endOfTheDay()
            database.queryCollection(
                path = PARKING_REQUEST_PATH,
                query = {
                    REQUEST_EMAIL equalTo email and (REQUEST_DATE greaterThanOrEqualTo startOfTheDay) and (REQUEST_DATE lessThanOrEqualTo endOfTheDay)
                },
            ).map { document -> document.data<ParkingReservationDto>() }
        }

    override suspend fun getReservationsForDate(date: Long): Either<RepositoryError, List<ParkingReservationDto>> =
        safeDatabaseOperation {
            val startOfTheDay = date.startOfTheDay()
            val endOfTheDay = date.endOfTheDay()
            database.queryCollection(
                path = PARKING_REQUEST_PATH,
                query = {
                    REQUEST_DATE greaterThanOrEqualTo startOfTheDay and (REQUEST_DATE lessThanOrEqualTo endOfTheDay)
                },
            ).map { document -> document.data<ParkingReservationDto>() }
        }

    override fun observeReservationsForUser(
        email: String,
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
                REQUEST_EMAIL equalTo email and (REQUEST_DATE greaterThanOrEqualTo startOfTheDay) and (REQUEST_DATE lessThanOrEqualTo endOfTheDay)
            },
        ).map { list -> list.map { document -> document.data<ParkingReservationDto>().toDomain() } }
    }
}

private const val REQUEST_DATE = "date"
private const val REQUEST_EMAIL = "email"
private const val CREATED_AT = "createdAt"
