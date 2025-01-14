package org.product.inventory.web.pages.seatreservationtimeline

import com.varabyte.kobweb.compose.ui.graphics.Colors
import org.product.inventory.shared.MR
import org.product.inventory.web.components.toProfileInfo
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.toCssColor
import seatreservation.model.ReservableDate
import seatreservation.model.isCancelOption
import user.model.InventoryAppUser

class ReservableDateUiItemMapper(
    private val dictionary: Dictionary,
) {

    private val cancelReservableDateButtonUi by lazy {
        ReservableDateButtonUi(
            background = MR.colors.disabled.toCssColor(),
            textButtonColor = MR.colors.textBlack.toCssColor(),
            buttonText = dictionary.get(StringRes.seatReservationTimelineCancelButtonText),
        )
    }

    private val weekendReservableDateButtonUi by lazy {
        ReservableDateButtonUi(
            background = MR.colors.disabled.toCssColor(),
            textButtonColor = MR.colors.textLight.toCssColor(),
            buttonText = dictionary.get(StringRes.seatReservationTimelineWeekendButtonText),
            isEnabled = false,
        )
    }

    fun buildReservableDateItemUi(
        reservableDate: ReservableDate,
        currentUser: InventoryAppUser,
    ) = when (reservableDate) {
        is ReservableDate.Weekday -> buildWeekdayReservableDateItemUi(reservableDate, currentUser)
        is ReservableDate.Weekend -> buildWeekendReservableDateItemUi(reservableDate)
    }

    private fun buildWeekdayReservableDateItemUi(
        reservableDate: ReservableDate.Weekday,
        currentUser: InventoryAppUser,
    ) = with(reservableDate) {
        ReservableDateUiItem.Weekday(
            dateUi = DateUi(
                day = day,
                date = date,
                background = MR.colors.secondary.toCssColor(),
                textColor = MR.colors.surface.toCssColor(),
            ),
            seatHolders = seatHolders.map { it.toProfileInfo(dictionary) },
            remainingSeats = remainingSeats,
            remainingSeatsText = dictionary.get(StringRes.seatReservationTimelineSeatsReaminingText),
            buttonUi = getButtonUi(this, currentUser),
            option = getReservableActionOption(this, currentUser),
        )
    }

    private fun getButtonUi(
        reservableDate: ReservableDate.Weekday,
        currentUser: InventoryAppUser,
    ) = if (reservableDate.isCancelOption(currentUser.id)) {
        cancelReservableDateButtonUi
    } else {
        buildReservableButtonUi(!reservableDate.isFullyReserved)
    }

    private fun buildReservableButtonUi(
        isEnabled: Boolean,
    ) = ReservableDateButtonUi(
        background = if (isEnabled) MR.colors.secondary.toCssColor() else MR.colors.secondary.toCssColor().copyf(alpha = 0.5f),
        textButtonColor = if (isEnabled) MR.colors.surface.toCssColor() else Colors.White.copyf(alpha = 0.5f),
        buttonText = dictionary.get(StringRes.seatReservationTimelineReserveButtonText),
        isEnabled = isEnabled,
    )

    private fun getReservableActionOption(
        reservableDate: ReservableDate.Weekday,
        currentUser: InventoryAppUser,
    ) = if (reservableDate.isCancelOption(currentUser.id)) {
        ReservableActionOption.CANCEL
    } else {
        ReservableActionOption.RESERVE
    }

    private fun buildWeekendReservableDateItemUi(
        reservableDate: ReservableDate.Weekend,
    ) = ReservableDateUiItem.Weekend(
        dateUi = DateUi(
            day = reservableDate.day,
            date = reservableDate.date,
            background = MR.colors.disabled.toCssColor(),
            textColor = MR.colors.textLight.toCssColor(),
        ),
        buttonUi = weekendReservableDateButtonUi,
    )
}
