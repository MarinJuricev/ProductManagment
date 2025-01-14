package parking.reservation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import parking.reservation.model.ParkingReservation
import parking.reservation.repository.ParkingReservationRepository
import user.usecase.ObserveCurrentUser

class ObserveMyParkingRequests(
    private val repository: ParkingReservationRepository,
    private val observeCurrentUser: ObserveCurrentUser,
) {

    operator fun invoke(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ParkingReservation>> = observeCurrentUser()
        .flatMapLatest {
            repository.observeReservationsForUser(
                email = it.email,
                startDate = startDate,
                endDate = endDate,
            )
        }
}
