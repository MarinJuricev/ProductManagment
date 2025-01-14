package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

data class FilterData(
    val status: ParkingReservationStatusUi? = null,
    val userEmail: String = "",
)
