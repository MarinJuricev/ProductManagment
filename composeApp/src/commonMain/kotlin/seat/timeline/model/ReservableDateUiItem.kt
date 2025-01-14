package seat.timeline.model

import user.model.InventoryAppUser

sealed interface ReservableDateUiItem {
    data class Weekend(val date: String) : ReservableDateUiItem
    data class Weekday(
        val date: String,
        val seatHolders: List<InventoryAppUser>,
        val remainingSeats: String,
        val remainingSeatsText: String,
        val option: ReservableDateUiItemOption,
    ) : ReservableDateUiItem

    data object Retry : ReservableDateUiItem
    data object Loading : ReservableDateUiItem
}

sealed class ReservableDateUiItemOption(val optionTitle: String) {
    data class Cancel(
        val title: String,
        val officeId: String,
        val date: Long,
    ) : ReservableDateUiItemOption(optionTitle = title)

    data class Reserve(
        val title: String,
        val enabled: Boolean,
        val officeId: String,
        val date: Long,
    ) : ReservableDateUiItemOption(optionTitle = title)
}
