package parking.usersRequests.screenComponent.header.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.InventoryDropDownPicker
import components.InventoryTextField
import org.product.inventory.shared.MR
import parking.reservation.components.FormDatePicker
import parking.reservation.components.ParkingReservationStatusIndicator
import parking.reservation.model.ParkingReservationStatusUiModel
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent.DateRangeChanged
import parking.usersRequests.screenComponent.header.filter.interaction.FilterViewEffect.DateRangeFilterApplied
import parking.usersRequests.screenComponent.header.filter.interaction.FilterViewEffect.EmailFilterApplied
import parking.usersRequests.screenComponent.header.filter.interaction.FilterViewEffect.StatusFilterApplied

@Composable
fun UserRequestsFilterScreenComponent(
    onValidDateRangeSelected: (startDate: Long, endDate: Long) -> Unit,
    onStatusSelected: (ParkingReservationStatusUiModel?) -> Unit,
    onEmailChanged: (String) -> Unit,
    filterViewModel: FilterViewModel,
) {
    val uiState by filterViewModel.filterState.collectAsState()

    LaunchedEffect(Unit) {
        filterViewModel.viewEffect.collect {
            when (it) {
                is DateRangeFilterApplied -> onValidDateRangeSelected(it.startDate, it.endDate)
                is StatusFilterApplied -> onStatusSelected(it.status)
                is EmailFilterApplied -> onEmailChanged(it.email)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .background(colorResource(MR.colors.surface.resourceId))
            .padding(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FormDatePicker(
                modifier = Modifier.fillMaxWidth(0.5f).padding(horizontal = 5.dp),
                title = uiState.datePickerState.startDatePickerTitle,
                displayedDate = uiState.datePickerState.dateRangeData.displayedStartDate,
                selectedTimestamp = uiState.datePickerState.dateRangeData.selectedStartDateTimestamp,
                confirmButtonText = uiState.datePickerState.confirmButtonText,
                onDateSelected = { startDate ->
                    filterViewModel.onEvent(
                        DateRangeChanged(
                            startDate,
                            uiState.datePickerState.dateRangeData.selectedEndDateTimestamp,
                        ),
                    )
                },
                upperDateLimit = uiState.datePickerState.dateRangeData.upperDateLimit,
            )

            FormDatePicker(
                modifier = Modifier.fillMaxWidth().padding(5.dp),
                title = uiState.datePickerState.endDatePickerTitle,
                displayedDate = uiState.datePickerState.dateRangeData.displayedEndDate,
                selectedTimestamp = uiState.datePickerState.dateRangeData.selectedEndDateTimestamp,
                confirmButtonText = uiState.datePickerState.confirmButtonText,
                onDateSelected = { endDate ->
                    filterViewModel.onEvent(
                        DateRangeChanged(
                            uiState.datePickerState.dateRangeData.selectedStartDateTimestamp,
                            endDate,
                        ),
                    )
                },
                lowerDateLimit = uiState.datePickerState.dateRangeData.loverDateLimit,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            InventoryTextField(
                modifier = Modifier.fillMaxWidth(0.5f).padding(5.dp),
                value = uiState.searchEmail,
                onValueChanged = {
                    onEmailChanged(it)
                },
                placeholder = { BodySmallText(uiState.searchEmailPlaceholderText) },
                shape = RoundedCornerShape(12.dp),
                textPaddingValues = PaddingValues(vertical = 16.dp, horizontal = 10.dp),
            )

            InventoryDropDownPicker(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp),
                innerContentModifier = Modifier.fillMaxWidth(0.45f),
                menuItems = uiState.availableStatusTypes,
                selectedItem = uiState.selectedStatus,
                trailingIcon = MR.images.drop_down_icon,
                placeholder = { ParkingReservationStatusIndicator(null) },
                onItemClick = onStatusSelected,
            ) {
                ParkingReservationStatusIndicator(it)
            }
        }
    }
}
