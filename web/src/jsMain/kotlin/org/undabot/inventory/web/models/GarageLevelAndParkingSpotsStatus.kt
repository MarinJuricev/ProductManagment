package org.product.inventory.web.models

import parking.reservation.model.GarageLevelData

sealed interface GarageLevelAndParkingSpotsStatus {

    data object Loading : GarageLevelAndParkingSpotsStatus

    data object Error : GarageLevelAndParkingSpotsStatus

    data class Success(
        val garageLevelData: List<GarageLevelData>,
    ) : GarageLevelAndParkingSpotsStatus
}
