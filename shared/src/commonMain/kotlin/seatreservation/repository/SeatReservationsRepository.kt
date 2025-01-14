package seatreservation.repository

import arrow.core.Either
import core.model.RepositoryError
import kotlinx.coroutines.flow.Flow
import seatreservation.model.SeatReservation
import seatreservation.model.SeatReservationError

interface SeatReservationsRepository {
    fun observeSeatReservationsFor(
        officeId: String,
        fromDate: Long,
        toDate: Long,
    ): Flow<List<SeatReservation>>

    suspend fun getSeatReservationsFor(
        officeId: String,
        date: Long,
    ): Either<SeatReservationError, List<SeatReservation>>

    suspend fun getSeatReservation(
        officeId: String,
        date: Long,
        userId: String,
    ): Either<SeatReservationError, SeatReservation>

    suspend fun reserveSeatFor(seatReservation: SeatReservation): Either<RepositoryError, Unit>

    suspend fun cancelSeatReservationFor(id: String): Either<RepositoryError, Unit>
}
