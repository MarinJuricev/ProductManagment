package org.product.inventory.web.pages.seatreservation

import seatreservation.dashboard.model.SeatReservationOption

sealed interface SeatReservationEvent {

    data class PathClick(val path: String) : SeatReservationEvent

    data class ItemClick(val type: SeatReservationOption) : SeatReservationEvent
}
