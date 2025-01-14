package core.utils

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format
import org.product.inventory.shared.MR
fun generalDeleteQuestion(input: String): StringDesc = MR.strings.general_delete_question.format(input)

fun generalCancelQuestion(input: String): StringDesc = MR.strings.general_cancel_question.format(input)

fun slotsManagementRequestDeleteSuccess(input: String): StringDesc = MR.strings.slots_management_request_delete_success.format(input)

fun garageLevelCreatorDeleteLevelQuestion(input: String): StringDesc = MR.strings.garage_level_creator_delete_level_question.format(input)

fun seatReservationRemainingSeats(quantity: Int): StringDesc {
    // we pass quantity as selector for correct plural string and for pass quantity as argument for formatting
    return MR.plurals.seat_reservation_remaining_seats.format(quantity, quantity)
}

fun myParkingReservationCancelConfirmationTitle(input: String): StringDesc = MR.strings.my_parking_reservation_cancel_confirmation_title.format(input)

fun lateParkingReservation(
    email: String,
    slackChannel: String,
): StringDesc = MR.strings.parking_reservation_new_request_late_reservation_message.format(email, slackChannel)

fun deleteOffice(name: String): StringDesc = MR.strings.seat_reservation_delete_office_message.format(name)
