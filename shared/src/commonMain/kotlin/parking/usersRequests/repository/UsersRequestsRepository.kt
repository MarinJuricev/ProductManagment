package parking.usersRequests.repository

import arrow.core.Either
import core.model.RepositoryError
import kotlinx.coroutines.flow.Flow
import parking.reservation.model.ParkingReservation

interface UsersRequestsRepository {
    suspend fun getUsersRequests(
        startDate: Long,
        endDate: Long,
    ): Either<RepositoryError, List<ParkingReservation>>

    suspend fun getApprovedUsersRequests(): Either<RepositoryError, List<ParkingReservation>>

    fun observeUserRequests(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ParkingReservation>>

    suspend fun updateUserRequest(parkingReservation: ParkingReservation): Either<RepositoryError, Unit>
}
