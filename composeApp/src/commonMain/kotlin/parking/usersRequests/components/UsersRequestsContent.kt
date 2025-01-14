package parking.usersRequests.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.InventoryBigButton
import components.LoadingIndicator
import parking.reservation.model.ParkingReservationUiModel
import parking.usersRequests.interaction.UsersRequestsEvent
import parking.usersRequests.interaction.UsersRequestsEvent.DateRangeSelect
import parking.usersRequests.interaction.UsersRequestsEvent.EmailFilterApplied
import parking.usersRequests.interaction.UsersRequestsEvent.StatusFilterApplied
import parking.usersRequests.interaction.UsersRequestsScreenState
import parking.usersRequests.screenComponent.header.filter.FilterViewModel
import parking.usersRequests.screenComponent.header.filter.UserRequestsFilterScreenComponent
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent.SearchEmailChanged
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent.StatusChanged

@Composable
fun UsersRequestsContent(
    uiState: UsersRequestsScreenState,
    onItemClick: (ParkingReservationUiModel) -> Unit,
    onEvent: (UsersRequestsEvent) -> Unit,
    filterViewModel: FilterViewModel,
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            UserRequestsFilterScreenComponent(
                onValidDateRangeSelected = { startDate, endDate ->
                    onEvent(DateRangeSelect(startDate, endDate))
                },
                filterViewModel = filterViewModel,
                onStatusSelected = {
                    filterViewModel.onEvent(StatusChanged(it))
                    onEvent(StatusFilterApplied(it))
                },
                onEmailChanged = {
                    filterViewModel.onEvent(SearchEmailChanged(it))
                    onEvent(EmailFilterApplied(it))
                },
            )
        }

        when (uiState) {
            is UsersRequestsScreenState.Content -> {
                items(uiState.requests) {
                    ParkingReservationRequestItem(
                        modifier = Modifier.padding(vertical = 8.dp),
                        reservation = it,
                        onItemClick = onItemClick,
                    )
                }
            }

            is UsersRequestsScreenState.EmptyState -> {
                item {
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
            }

            is UsersRequestsScreenState.Retry -> {
                item {
                    InventoryBigButton(onClick = {}) {
                        BodySmallText("Retry")
                    }
                }
            }

            UsersRequestsScreenState.Loading -> item { LoadingIndicator() }
        }
    }
}
