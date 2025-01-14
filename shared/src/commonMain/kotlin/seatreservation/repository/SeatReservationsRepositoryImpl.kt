package seatreservation.repository

import arrow.core.Either
import arrow.core.raise.either
import core.model.RepositoryError
import core.utils.endOfTheDay
import core.utils.safeDatabaseOperation
import core.utils.startOfTheDay
import database.Database
import database.path.SEAT_RESERVATIONS_PATH
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import seatreservation.model.SeatReservation
import seatreservation.model.SeatReservationDto
import seatreservation.model.SeatReservationError
import seatreservation.model.SeatReservationError.ReservationNotFound

internal class SeatReservationsRepositoryImpl(
    private val database: Database,
) : SeatReservationsRepository {
    override fun observeSeatReservationsFor(
        officeId: String,
        fromDate: Long,
        toDate: Long,
    ): Flow<List<SeatReservation>> = database
        .observeCollection(
            path = SEAT_RESERVATIONS_PATH,
            query = {
                OFFICE_ID equalTo officeId and (DATE greaterThanOrEqualTo fromDate.startOfTheDay() and (DATE lessThanOrEqualTo toDate.endOfTheDay()))
            },
        )
        .map { list -> list.map { document -> document.data<SeatReservationDto>().toDomain() } }

    override suspend fun getSeatReservationsFor(
        officeId: String,
        date: Long,
    ): Either<SeatReservationError, List<SeatReservation>> = either {
        database.queryCollection(
            path = SEAT_RESERVATIONS_PATH,
            query = {
                OFFICE_ID equalTo officeId and (DATE greaterThanOrEqualTo date.startOfTheDay() and (DATE lessThanOrEqualTo date.endOfTheDay()))
            },
        ).map { document -> document.data<SeatReservationDto>().toDomain() }
    }

    override suspend fun getSeatReservation(
        officeId: String,
        date: Long,
        userId: String,
    ): Either<SeatReservationError, SeatReservation> = either {
        database.queryCollection(
            path = SEAT_RESERVATIONS_PATH,
            query = {
                USER_ID equalTo userId and (OFFICE_ID equalTo officeId) and (DATE greaterThanOrEqualTo date.startOfTheDay() and (DATE lessThanOrEqualTo date.endOfTheDay()))
            },
        )
            .map { document -> document.data<SeatReservationDto>().toDomain() }
            .firstOrNull() ?: raise(ReservationNotFound)
    }

    override suspend fun reserveSeatFor(seatReservation: SeatReservation): Either<RepositoryError, Unit> = safeDatabaseOperation {
        database.save(
            id = seatReservation.id,
            path = SEAT_RESERVATIONS_PATH,
            data = seatReservation.toDto(),
        )
    }

    override suspend fun cancelSeatReservationFor(id: String): Either<RepositoryError, Unit> = safeDatabaseOperation {
        database.deleteDocument(
            id = id,
            path = SEAT_RESERVATIONS_PATH,
        )
    }
}

private const val OFFICE_ID = "officeId"
private const val DATE = "date"
private const val USER_ID = "userId"
