package seat.management.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import seat.management.interaction.SeatManagementScreenTexts

class SeatManagementTextMapper(
    private val dictionary: Dictionary,
) {
    fun map() = SeatManagementScreenTexts(
        officeNameLabel = dictionary.getString(MR.strings.seat_reservation_office_title),
        seatsAmountLabel = dictionary.getString(MR.strings.seat_reservation_seats_title),
        officeNamePlaceHolder = dictionary.getString(MR.strings.seat_reservation_office_name_placeholder),
    )
}
