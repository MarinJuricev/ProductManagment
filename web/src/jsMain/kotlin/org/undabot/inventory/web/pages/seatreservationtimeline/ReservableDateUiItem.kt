package org.product.inventory.web.pages.seatreservationtimeline

import com.varabyte.kobweb.compose.ui.graphics.Color
import org.product.inventory.web.components.ProfileInfo

sealed interface ReservableDateUiItem {

    val dateUi: DateUi
    val buttonUi: ReservableDateButtonUi

    data class Weekday(
        override val dateUi: DateUi,
        val seatHolders: List<ProfileInfo>,
        val remainingSeats: Int,
        val remainingSeatsText: String,
        override val buttonUi: ReservableDateButtonUi,
        val option: ReservableActionOption,
    ) : ReservableDateUiItem

    data class Weekend(
        override val dateUi: DateUi,
        override val buttonUi: ReservableDateButtonUi,
    ) : ReservableDateUiItem
}

data class DateUi(
    val day: String,
    val date: Long,
    val textColor: Color,
    val background: Color,
)

data class ReservableDateButtonUi(
    val background: Color.Rgb,
    val textButtonColor: Color.Rgb,
    val buttonText: String,
    val isEnabled: Boolean = true,
)

enum class ReservableActionOption {
    RESERVE,
    CANCEL,
}
