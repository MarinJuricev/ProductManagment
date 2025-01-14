package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

data class EditableFields(
    val approveNote: String? = null,
    val rejectReason: String? = null,
    val statusUi: ParkingReservationStatusUi? = null,
    val garageLevel: ParkingOption? = null,
    val parkingSpot: ParkingOption? = null,
    val hasPermanentGarageAccess: Boolean = false,
)
