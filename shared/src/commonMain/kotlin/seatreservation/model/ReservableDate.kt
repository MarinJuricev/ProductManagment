package seatreservation.model

import user.model.InventoryAppUser

sealed class ReservableDate(
    open val day: String,
    open val date: Long,
) {
    data class Weekend(
        override val day: String,
        override val date: Long,
    ) : ReservableDate(day, date)

    data class Weekday(
        override val day: String,
        override val date: Long,
        val seatHolders: List<InventoryAppUser>,
        val remainingSeats: Int,
    ) : ReservableDate(day, date) {
        val isFullyReserved: Boolean = remainingSeats <= 0
    }
}

fun ReservableDate.Weekday.isCancelOption(userId: String) = seatHolders.any { it.id == userId }
