package parking.usersRequests.screenComponent.header.datepicker.interaction

sealed interface DateRangePickerViewEffect {
    data class ValidationSucceeded(val startDate: Long, val endDate: Long) :
        DateRangePickerViewEffect
}
