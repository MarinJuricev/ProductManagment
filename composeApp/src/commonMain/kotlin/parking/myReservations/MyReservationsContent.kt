package parking.myReservations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.GeneralRetry
import components.LoadingIndicator
import parking.myReservations.interaction.MyReservationsEvent
import parking.myReservations.interaction.MyReservationsEvent.RetryClick
import parking.myReservations.interaction.MyReservationsUiState
import parking.myReservations.interaction.MyReservationsUiState.Content
import parking.myReservations.interaction.MyReservationsUiState.EmptyState
import parking.myReservations.interaction.MyReservationsUiState.Loading
import parking.myReservations.interaction.MyReservationsUiState.Retry
import parking.reservation.model.ParkingReservationUiModel
import parking.usersRequests.components.NoDataFound
import parking.usersRequests.screenComponent.header.datepicker.DateRangePickerScreenComponent
import parking.usersRequests.screenComponent.header.datepicker.DateRangePickerViewModel

@Composable
fun MyReservationsContent(
    uiState: MyReservationsUiState,
    onEvent: (MyReservationsEvent) -> Unit,
    onItemClick: (ParkingReservationUiModel) -> Unit,
    dateRangePickerViewModel: DateRangePickerViewModel,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            DateRangePickerScreenComponent(
                onValidDateRangeSelected = { startDate, endDate ->
                    onEvent(
                        MyReservationsEvent.DateRangeSelect(
                            startDate,
                            endDate,
                        ),
                    )
                },
                viewModel = dateRangePickerViewModel,
            )
        }

        when (uiState) {
            is Content -> items(uiState.requests) {
                MyReservationRequestItem(
                    modifier = Modifier.padding(vertical = 8.dp),
                    reservation = it,
                    onItemClick = onItemClick,
                )
            }

            is EmptyState -> item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(),
                ) {
                    NoDataFound(
                        errorTitle = uiState.emptyStateTitle,
                        errorMessage = uiState.emptyStateMessage,
                    )
                }
            }

            is Loading -> item { LoadingIndicator() }

            is Retry -> item {
                GeneralRetry(onRetryClick = { onEvent(RetryClick) })
            }
        }
    }
}
