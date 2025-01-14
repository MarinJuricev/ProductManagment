package parking.usersRequests.details.model

import parking.reservation.model.ParkingReservationStatus

data class ReservationDetailsEditableFields(
    val adminNote: String? = null,
    val currentStatus: ParkingReservationStatus? = null,
    val garageLevelUi: GarageLevelUi? = null,
    val garageSpotUi: GarageSpotUi? = null,
    val saveButtonLoading: Boolean = false,
    val hasGarageAccess: Boolean = false,
)
