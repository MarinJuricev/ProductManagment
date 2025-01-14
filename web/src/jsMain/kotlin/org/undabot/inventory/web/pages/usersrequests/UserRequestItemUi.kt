package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

data class UserRequestItemUi(
    val id: String,
    val emailLabel: String,
    val email: String,
    val requestDateLabel: String,
    val requestedDateMillis: Long,
    val requestedDate: String,
    val submittedDateLabel: String,
    val submittedDate: String,
    val status: ParkingReservationStatusUi,
)
