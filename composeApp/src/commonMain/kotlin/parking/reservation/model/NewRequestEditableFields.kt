package parking.reservation.model

import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi
import utils.getMillisOfTomorrow

data class NewRequestEditableFields(
    val selectedGarageLevel: GarageLevelUi = GarageLevelUi.Undefined,
    val selectedGarageSpot: GarageSpotUi = GarageSpotUi.Undefined,
    val hasGarageAccess: Boolean = false,
    val notes: String = "",
    val selectedDate: Long = getMillisOfTomorrow(),
    val submitButtonLoading: Boolean = false,
)
