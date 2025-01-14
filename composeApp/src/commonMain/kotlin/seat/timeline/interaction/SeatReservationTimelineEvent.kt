package seat.timeline.interaction

import seat.timeline.model.ReservableDateUiItemOption

sealed interface SeatReservationTimelineEvent {
    data class OfficeChanged(val office: OfficeItem) : SeatReservationTimelineEvent
    data object OnRetryClick : SeatReservationTimelineEvent
    data class OnOptionClick(val option: ReservableDateUiItemOption) : SeatReservationTimelineEvent
}
