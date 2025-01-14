package org.product.inventory.web.pages.parkingreservation

sealed interface ParkingReservationEvent {

    data class ReservationItemClick(val type: ReservationItemType) : ParkingReservationEvent

    data object RetryClick : ParkingReservationEvent

    data class PathClick(val path: String) : ParkingReservationEvent
}
