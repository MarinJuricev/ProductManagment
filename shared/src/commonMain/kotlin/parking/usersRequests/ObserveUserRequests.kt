package parking.usersRequests

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import parking.reservation.model.ParkingReservation
import parking.usersRequests.repository.UsersRequestsRepository
import user.usecase.ObserveCurrentUser

class ObserveUserRequests(
    private val repository: UsersRequestsRepository,
    private val observeCurrentUser: ObserveCurrentUser,
) {
    operator fun invoke(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ParkingReservation>> = observeCurrentUser()
        .flatMapLatest {
            repository.observeUserRequests(
                startDate = startDate,
                endDate = endDate,
            )
        }
}
