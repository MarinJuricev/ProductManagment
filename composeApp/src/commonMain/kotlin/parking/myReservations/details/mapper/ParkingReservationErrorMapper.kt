package parking.myReservations.details.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.ParkingReservationError

class ParkingReservationErrorMapper(
    private val dictionary: Dictionary,
) {
    fun map(error: ParkingReservationError) = when (error) {
        is ParkingReservationError.ErrorMessage -> error.message
        is ParkingReservationError.InvalidEmailFormat -> dictionary.getString(MR.strings.general_email_validation_error)
        is ParkingReservationError.OnlyFutureDateReservationsAllowed -> dictionary.getString(MR.strings.parking_reservation_future_date_error)
        ParkingReservationError.DuplicateReservation -> dictionary.getString(MR.strings.parking_reservation_duplicate_reservation_error)
        ParkingReservationError.LateReservation -> dictionary.getString(MR.strings.parking_reservation_late_reservation_error)
        is ParkingReservationError.Unauthorized -> dictionary.getString(MR.strings.general_unauthorized_error)
        is ParkingReservationError.UnknownError -> dictionary.getString(MR.strings.general_unknown_error_title)
    }
}
