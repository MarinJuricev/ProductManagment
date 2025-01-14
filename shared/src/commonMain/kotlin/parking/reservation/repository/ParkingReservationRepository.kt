package parking.reservation.repository

import arrow.core.Either
import core.model.RepositoryError
import kotlinx.coroutines.flow.Flow
import parking.reservation.model.ParkingRequest.Request
import parking.reservation.model.ParkingRequest.Reservation
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationDto

interface ParkingReservationRepository {
    suspend fun createRequest(request: Request): Either<RepositoryError, Unit>
    suspend fun createReservation(reservation: Reservation): Either<RepositoryError, ParkingReservationDto>
    suspend fun getReservationForId(id: String): Either<RepositoryError, ParkingReservationDto>
    suspend fun getReservationsForUser(
        email: String,
        startDate: Long,
        endDate: Long,
    ): Either<RepositoryError, List<ParkingReservationDto>>
    suspend fun getReservationsForDate(date: Long): Either<RepositoryError, List<ParkingReservationDto>>
    fun observeReservationsForUser(
        email: String,
        startDate: Long,
        endDate: Long,
    ): Flow<List<ParkingReservation>>
}
