package parking.reservation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import parking.reservation.model.GarageLevelData
import parking.reservation.repository.GarageLevelRepository
import user.usecase.ObserveCurrentUser

class ObserveGarageLevelsData(
    private val repository: GarageLevelRepository,
    private val observeCurrentUser: ObserveCurrentUser,
) {

    operator fun invoke(): Flow<List<GarageLevelData>> = observeCurrentUser()
        .flatMapLatest {
            repository.observeGarageLevels()
        }
}
