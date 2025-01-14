package seat.timeline.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import seat.timeline.model.ReservableDateUiItem
import seat.timeline.model.ReservableDateUiItemOption
import seatreservation.model.Office
import seatreservation.model.ReservableDate
import user.model.InventoryAppUser
import utils.convertMillisToDate

class ReservableDateUiItemMapper(
    private val dictionary: Dictionary,
) {
    fun map(
        reservableDate: ReservableDate,
        currentUser: InventoryAppUser,
        selectedOffice: Office,
    ): ReservableDateUiItem = when (reservableDate) {
        is ReservableDate.Weekday -> {
            val rawText = dictionary.getPluralStringResource(
                pluralsResource = MR.plurals.seat_reservation_remaining_seats,
                count = reservableDate.remainingSeats,
            )
            val count = rawText.split(Regex("\\D+")).firstOrNull().orEmpty()
            val text = rawText.split(Regex("\\d+")).firstOrNull { it.isNotBlank() }.orEmpty()
            ReservableDateUiItem.Weekday(
                date = convertMillisToDate(reservableDate.date),
                seatHolders = reservableDate.seatHolders,
                remainingSeats = count,
                remainingSeatsText = text,
                option = getOption(reservableDate, currentUser, selectedOffice),
            )
        }

        is ReservableDate.Weekend -> ReservableDateUiItem.Weekend(
            date = convertMillisToDate(
                reservableDate.date,
            ),
        )
    }

    private fun getOption(
        reservableDate: ReservableDate.Weekday,
        currentUser: InventoryAppUser,
        office: Office,
    ) =
        if (reservableDate.seatHolders.any { it.id == currentUser.id }) {
            ReservableDateUiItemOption.Cancel(
                title = dictionary.getString(MR.strings.seat_reservation_cancel_reservation_button),
                officeId = office.id,
                date = reservableDate.date,
            )
        } else {
            ReservableDateUiItemOption.Reserve(
                title = dictionary.getString(MR.strings.seat_reservation_reserve_seat_button),
                enabled = !reservableDate.isFullyReserved,
                officeId = office.id,
                date = reservableDate.date,
            )
        }
}
