package parking.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.DashboardUiItem
import components.LoadingIndicator
import components.RetryScreen
import parking.crewManagement.CrewManagementScreen
import parking.dashboard.interaction.ParkingDashboardScreenEvent
import parking.dashboard.interaction.ParkingDashboardScreenEvent.DashboardOptionSelect
import parking.dashboard.interaction.ParkingDashboardScreenEvent.RetryButtonClick
import parking.dashboard.interaction.ParkingDashboardScreenState.Content
import parking.dashboard.interaction.ParkingDashboardScreenState.Loading
import parking.dashboard.interaction.ParkingDashboardScreenState.Retry
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToCrewManagement
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToEmailTemplates
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToMyReservations
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToNewRequest
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToSlotsManagement
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToUserRequests
import parking.emailTemplates.EmailTemplatesScreen
import parking.myReservations.MyReservationsScreen
import parking.reservation.CreateParkingReservationScreen
import parking.slotsManagement.SlotsManagementScreen
import parking.usersRequests.UsersRequestsScreen

class ParkingDashboardScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ParkingDashboardViewModel = koinScreenModel()

        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    is NavigateToCrewManagement -> navigator.push(CrewManagementScreen())
                    is NavigateToEmailTemplates -> navigator.push(EmailTemplatesScreen())
                    is NavigateToMyReservations -> navigator.push(MyReservationsScreen())
                    is NavigateToNewRequest -> navigator.push(CreateParkingReservationScreen())
                    is NavigateToSlotsManagement -> navigator.push(SlotsManagementScreen())
                    is NavigateToUserRequests -> navigator.push(UsersRequestsScreen())
                }
            }
        }

        when (val uiState = viewModel.uiState.collectAsState().value) {
            is Content -> DashboardContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )

            is Loading -> LoadingIndicator()
            is Retry -> RetryScreen(
                title = uiState.title,
                description = uiState.description,
                buttonText = uiState.buttonText,
                onRetryClick = { viewModel.onEvent(RetryButtonClick) },
            )
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: Content,
    onEvent: (ParkingDashboardScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = uiState.items,
            key = { it.title.resourceId },
        ) { option ->
            DashboardUiItem(
                item = option,
                onItemClick = { onEvent(DashboardOptionSelect(option)) },
            )
        }
    }
}
