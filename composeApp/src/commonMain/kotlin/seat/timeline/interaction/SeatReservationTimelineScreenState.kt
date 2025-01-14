package seat.timeline.interaction

import seat.timeline.model.ReservableDateUiItem

sealed interface SeatReservationTimelineScreenState {
    data object Loading : SeatReservationTimelineScreenState
    data object Retry : SeatReservationTimelineScreenState
    data class Content(
        val selectedOffice: OfficeItem,
        val availableOffices: List<OfficeItem>,
        val reservableDateUi: List<ReservableDateUiItem>,
    ) : SeatReservationTimelineScreenState
}
