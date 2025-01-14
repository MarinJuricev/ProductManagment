package parking.reservation.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.reservation.model.ParkingReservationStatus.Canceled
import parking.reservation.model.ParkingReservationStatus.Declined
import parking.reservation.model.ParkingReservationStatus.Submitted
import parking.reservation.model.ParkingReservationUiModel
import utils.convertMillisToDate

class ParkingReservationUiModelMapper(
    private val dictionary: Dictionary,
    private val statusMapper: ParkingReservationStatusUiMapper,
) {
    operator fun invoke(parkingReservation: ParkingReservation) = ParkingReservationUiModel(
        id = parkingReservation.id,
        email = parkingReservation.email,
        date = convertMillisToDate(parkingReservation.date),
        dateTimeStamp = parkingReservation.date,
        note = parkingReservation.note,
        adminNote = when (val status = parkingReservation.status) {
            is Approved -> status.adminNote
            is Declined -> status.adminNote
            Canceled -> ""
            Submitted -> ""
        },
        reservationStatus = statusMapper(parkingReservation.status),
        createdAt = convertMillisToDate(parkingReservation.createdAt),
        updatedAt = convertMillisToDate(parkingReservation.updatedAt),
        emailFieldName = dictionary.getString(MR.strings.parking_reservation_item_email),
        requestedDateFieldName = dictionary.getString(MR.strings.parking_reservation_item_requested_date),
        createdAtTimeStamp = parkingReservation.createdAt,
        submittedDateFieldName = dictionary.getString(MR.strings.my_parking_reservation_submitted),
        updatedAtTimeStamp = parkingReservation.updatedAt,
    )
}
