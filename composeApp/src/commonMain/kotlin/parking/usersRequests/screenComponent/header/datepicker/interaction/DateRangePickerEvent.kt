package parking.usersRequests.screenComponent.header.datepicker.interaction

sealed interface DateRangePickerEvent {
    data class DateRangeChanged(val startDate: Long, val endDate: Long) : DateRangePickerEvent
}
