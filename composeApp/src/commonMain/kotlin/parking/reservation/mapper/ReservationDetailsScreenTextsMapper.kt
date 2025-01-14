package parking.reservation.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.ParkingReservationDetailsScreenTexts

class ReservationDetailsScreenTextsMapper(private val dictionary: Dictionary) {
    fun map() = ParkingReservationDetailsScreenTexts(
        additionalNotesFormTitle = dictionary.getString(MR.strings.parking_reservation_new_request_additional_notes),
        dateFormTitle = dictionary.getString(MR.strings.parking_reservation_new_request_date),
        garageLevelPickerTitle = dictionary.getString(MR.strings.parking_reservation_item_garage_level),
        garageParkingSpotPickerTitle = dictionary.getString(MR.strings.parking_reservation_item_parking_spot),
        additionNotesFormPlaceholderText = dictionary.getString(MR.strings.parking_reservation_additional_notes_empty_state),
        saveButtonText = dictionary.getString(MR.strings.parking_reservation_user_request_save),
        noSpaceLeftMessage = dictionary.getString(MR.strings.parking_reservation_no_free_spaces_error),
    )
}
