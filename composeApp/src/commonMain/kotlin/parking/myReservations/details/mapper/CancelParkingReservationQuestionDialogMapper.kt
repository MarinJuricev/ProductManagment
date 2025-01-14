package parking.myReservations.details.mapper

import components.QuestionDialogData
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class CancelParkingReservationQuestionDialogMapper(
    private val dictionary: Dictionary,
) {
    fun map(date: String) = QuestionDialogData(
        isVisible = true,
        title = dictionary.getString(MR.strings.general_cancel_question, date),
        question = dictionary.getString(MR.strings.my_parking_reservation_cancel_confirmation_title)
            .format(date),
        negativeActionText = dictionary.getString(MR.strings.my_parking_reservation_cancel_confirmation_dismiss),
        positiveActionText = dictionary.getString(MR.strings.my_parking_reservation_cancel_confirmation_accept),
    )
}
