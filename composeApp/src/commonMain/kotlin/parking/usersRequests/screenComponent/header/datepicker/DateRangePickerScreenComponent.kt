package parking.usersRequests.screenComponent.header.datepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import parking.usersRequests.screenComponent.header.datepicker.component.DateRangePicker
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerEvent.DateRangeChanged
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerViewEffect.ValidationSucceeded

@Composable
fun DateRangePickerScreenComponent(
    onValidDateRangeSelected: (startDate: Long, endDate: Long) -> Unit,
    viewModel: DateRangePickerViewModel,
) {
    val pickerState by viewModel.pickerState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect {
            when (it) {
                is ValidationSucceeded -> onValidDateRangeSelected(it.startDate, it.endDate)
            }
        }
    }

    DateRangePicker(
        headerState = pickerState,
        onDateRangeChanged = { startDate, endDate ->
            viewModel.onEvent(
                DateRangeChanged(startDate, endDate),
            )
        },
        lowerDateLimit = pickerState.dateRangeData.loverDateLimit,
        upperDateLimit = pickerState.dateRangeData.upperDateLimit,
    )
}
