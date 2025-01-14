package parking.usersRequests.screenComponent.header.filter.interaction

import parking.reservation.model.ParkingReservationStatusUiModel
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerUiState

data class FilterState(
    val datePickerState: DateRangePickerUiState,
    val searchEmail: String,
    val searchEmailPlaceholderText: String,
    val availableStatusTypes: List<ParkingReservationStatusUiModel?>,
    val selectedStatus: ParkingReservationStatusUiModel?,
)
