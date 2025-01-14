package parking.usersRequests.screenComponent.header.datepicker.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR
import parking.reservation.components.FormDatePicker
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerUiState

@Composable
fun DateRangePicker(
    headerState: DateRangePickerUiState,
    modifier: Modifier = Modifier,
    onDateRangeChanged: (startDate: Long, endDate: Long) -> Unit,
    lowerDateLimit: Long = 0,
    upperDateLimit: Long = Long.MAX_VALUE,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .background(colorResource(MR.colors.surface.resourceId))
            .padding(horizontal = 8.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        FormDatePicker(
            title = headerState.startDatePickerTitle,
            displayedDate = headerState.dateRangeData.displayedStartDate,
            selectedTimestamp = headerState.dateRangeData.selectedStartDateTimestamp,
            confirmButtonText = headerState.confirmButtonText,
            onDateSelected = { startDate ->
                onDateRangeChanged(
                    startDate,
                    headerState.dateRangeData.selectedEndDateTimestamp,
                )
            },
            errorMessage = headerState.startDateError,
            upperDateLimit = upperDateLimit,
        )

        FormDatePicker(
            title = headerState.endDatePickerTitle,
            displayedDate = headerState.dateRangeData.displayedEndDate,
            selectedTimestamp = headerState.dateRangeData.selectedEndDateTimestamp,
            confirmButtonText = headerState.confirmButtonText,
            onDateSelected = { endDate ->
                onDateRangeChanged(
                    headerState.dateRangeData.selectedStartDateTimestamp,
                    endDate,
                )
            },
            errorMessage = headerState.endDateError,
            lowerDateLimit = lowerDateLimit,
        )
    }
}
