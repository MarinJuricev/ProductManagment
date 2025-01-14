package parking.reservation.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingReservationStatusUiModel

class ParkingReservationStatusUiMapper(
    val dictionary: Dictionary,
) {
    operator fun invoke(status: ParkingReservationStatus) = ParkingReservationStatusUiModel(
        status = status,
        text = when (status) {
            is ParkingReservationStatus.Approved -> dictionary.getString(MR.strings.parking_reservation_status_approved_label)
            is ParkingReservationStatus.Canceled -> dictionary.getString(MR.strings.parking_reservation_status_canceled_label)
            is ParkingReservationStatus.Declined -> dictionary.getString(MR.strings.parking_reservation_status_rejected_label)
            is ParkingReservationStatus.Submitted -> dictionary.getString(MR.strings.parking_reservation_status_submitted_label)
        },
    )
}
