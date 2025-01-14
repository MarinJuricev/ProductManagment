package seat.management.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import seat.management.components.EditOfficeDialogData
import seatreservation.model.Office

class EditOfficeDialogMapper(
    private val dictionary: Dictionary,
) {
    fun map(
        office: Office,
        officeName: String? = null,
        seats: String? = null,
    ): EditOfficeDialogData {
        val displayedName = officeName ?: office.title
        val displayedSeats = seats?.let { seats } ?: office.numberOfSeats.toString()
        return EditOfficeDialogData(
            title = dictionary.getString(MR.strings.slots_management_edit_title),
            office = Office(
                id = office.id,
                title = displayedName,
                numberOfSeats = seats?.toIntOrNull() ?: office.numberOfSeats,
            ),
            positiveOptionTitle = dictionary.getString(MR.strings.general_save),
            negativeOptionTitle = dictionary.getString(MR.strings.general_cancel),
            officeNameLabel = dictionary.getString(MR.strings.seat_reservation_office_name_label),
            seatsAmountLabel = dictionary.getString(MR.strings.seat_reservation_seats_title),
            saveEnabled = displayedName.isNotBlank() && displayedSeats.isNotBlank() && displayedSeats.toIntOrNull() != null && displayedSeats.toInt() > 0,
            seatsAmount = displayedSeats,
        )
    }
}
