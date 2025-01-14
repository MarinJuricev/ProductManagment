package seat.timeline.interaction

import seatreservation.model.ReservableDate

sealed interface ReservableDateUi {
    data object Loading : ReservableDateUi
    data class Content(val reservableDates: List<ReservableDate>) : ReservableDateUi
    data object Error : ReservableDateUi
}
