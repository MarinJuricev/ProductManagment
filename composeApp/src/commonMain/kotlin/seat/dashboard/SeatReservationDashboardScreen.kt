package seat.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.DashboardUiItem
import components.GeneralRetry
import components.LoadingIndicator
import seat.dashboard.interaction.SeatReservationDashboardScreenEvent
import seat.dashboard.interaction.SeatReservationDashboardScreenEvent.AdminDashboardOptionClick
import seat.dashboard.interaction.SeatReservationDashboardScreenEvent.RetryClick
import seat.dashboard.interaction.SeatReservationDashboardScreenState.Content
import seat.dashboard.interaction.SeatReservationDashboardScreenState.Loading
import seat.dashboard.interaction.SeatReservationDashboardScreenState.Retry
import seat.dashboard.interaction.SeatReservationDashboardViewEffect.OpenDashboardOption
import seat.dashboard.interaction.SeatReservationDashboardViewEffect.OpenTimelineAsUser
import seat.timeline.SeatReservationTimelineScreen

class SeatReservationDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: SeatReservationDashboardViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    is OpenTimelineAsUser -> navigator.replace(SeatReservationTimelineScreen())
                    is OpenDashboardOption -> navigator.push(it.screen)
                }
            }
        }

        when (val state = uiState) {
            is Content -> SeatReservationDashboardContent(
                uiState = state,
                onEvent = viewModel::onEvent,
            )

            is Loading -> LoadingIndicator()
            is Retry -> GeneralRetry(onRetryClick = { viewModel.onEvent(RetryClick) })
        }
    }
}

@Composable
private fun SeatReservationDashboardContent(
    uiState: Content,
    onEvent: (SeatReservationDashboardScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = uiState.options,
            key = { it.title.resourceId },
        ) { option ->
            DashboardUiItem(
                item = option,
                onItemClick = { onEvent(AdminDashboardOptionClick(option)) },
            )
        }
    }
}
