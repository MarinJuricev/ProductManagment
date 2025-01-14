package org.product.inventory.web.pages.seatreservationtimeline

import seatreservation.model.Office

sealed interface SeatReservationTimelineEvent {

    data class OnOfficeChanged(val office: Office) : SeatReservationTimelineEvent

    data class OnActionOptionClick(
        val date: Long,
        val option: ReservableActionOption,
        val officeId: String,
    ) : SeatReservationTimelineEvent
}
