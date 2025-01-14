package seat.management.mapper

import components.QuestionDialogData
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import seatreservation.model.Office

class DeleteOfficeDialogMapper(
    private val dictionary: Dictionary,
) {
    fun map(office: Office) = QuestionDialogData(
        isVisible = true,
        title = dictionary.getString(MR.strings.seat_reservation_delete_office_title),
        question = dictionary.getString(
            MR.strings.seat_reservation_delete_office_message,
            office.title,
        ),
        negativeActionText = dictionary.getString(MR.strings.general_cancel),
        positiveActionText = dictionary.getString(MR.strings.general_delete),
    )
}
