package parking.usersRequests.screenComponent.header.datepicker.interaction

data class DateRangePickerUiState(
    val startDatePickerTitle: String,
    val endDatePickerTitle: String,
    val dateRangeData: DateRangeData,
    val startDateError: String?,
    val endDateError: String?,
    val confirmButtonText: String,
)

data class DateRangeData(
    val selectedStartDateTimestamp: Long,
    val selectedEndDateTimestamp: Long,
    val displayedStartDate: String,
    val displayedEndDate: String,
    val loverDateLimit: Long,
    val upperDateLimit: Long,
)
