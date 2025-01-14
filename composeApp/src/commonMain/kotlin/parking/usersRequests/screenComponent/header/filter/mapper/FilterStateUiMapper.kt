package parking.usersRequests.screenComponent.header.filter.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.GarageLevel
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingReservationStatusUiModel
import parking.reservation.model.ParkingSpot
import parking.usersRequests.mapper.DateRangePickerUiMapper
import parking.usersRequests.screenComponent.header.filter.interaction.FilterState

class FilterStateUiMapper(
    private val dictionary: Dictionary,
    private val datePickerUiMapper: DateRangePickerUiMapper,
) {

    fun map() = FilterState(
        datePickerState = datePickerUiMapper.map(),
        searchEmail = "",
        searchEmailPlaceholderText = dictionary.getString(MR.strings.user_request_garage_email_filter_search),
        availableStatusTypes = listOf(
            null,
            ParkingReservationStatusUiModel(
                status = ParkingReservationStatus.Submitted,
                text = dictionary.getString(MR.strings.parking_reservation_status_submitted_label),
            ),
            ParkingReservationStatusUiModel(
                status = ParkingReservationStatus.Approved(
                    adminNote = "",
                    parkingCoordinate = ParkingCoordinate(GarageLevel("", ""), ParkingSpot("", "")),
                ),
                text = dictionary.getString(MR.strings.parking_reservation_status_approved_label),
            ),
            ParkingReservationStatusUiModel(
                status = ParkingReservationStatus.Declined(""),
                text = dictionary.getString(MR.strings.parking_reservation_status_rejected_label),
            ),
            ParkingReservationStatusUiModel(
                status = ParkingReservationStatus.Canceled,
                text = dictionary.getString(MR.strings.parking_reservation_status_canceled_label),
            ),
        ),
        selectedStatus = null,
    )
}
