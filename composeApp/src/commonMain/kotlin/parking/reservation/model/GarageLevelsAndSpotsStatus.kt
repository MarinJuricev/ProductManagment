package parking.reservation.model

import parking.usersRequests.details.model.GarageLevelUi

sealed interface GarageLevelsAndSpotsStatus {
    data object Loading : GarageLevelsAndSpotsStatus
    data object Failure : GarageLevelsAndSpotsStatus
    data class Success(val levels: List<GarageLevelUi>) : GarageLevelsAndSpotsStatus
}
