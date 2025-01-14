package seat.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import components.GeneralRetry
import components.InventoryDropDownPicker
import components.LoadingIndicator
import org.product.inventory.shared.MR
import seat.timeline.components.OfficeUiItem
import seat.timeline.components.ReservableDateUiItem
import seat.timeline.interaction.SeatReservationTimelineEvent
import seat.timeline.interaction.SeatReservationTimelineEvent.OfficeChanged
import seat.timeline.interaction.SeatReservationTimelineEvent.OnOptionClick
import seat.timeline.interaction.SeatReservationTimelineEvent.OnRetryClick
import seat.timeline.interaction.SeatReservationTimelineScreenState.Content
import seat.timeline.interaction.SeatReservationTimelineScreenState.Loading
import seat.timeline.interaction.SeatReservationTimelineScreenState.Retry
import seat.timeline.model.ReservableDateUiItem
import seat.timeline.model.ReservableDateUiItem.Weekday
import seat.timeline.model.ReservableDateUiItem.Weekend

class SeatReservationTimelineScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: SeatReservationTimelineViewModel = koinScreenModel()
        val uiState by viewModel.uiState.collectAsState()

        when (val state = uiState) {
            is Content -> SeatReservationTimelineScreenContent(
                uiState = state,
                onEvent = viewModel::onEvent,
            )

            is Loading -> LoadingIndicator()
            is Retry -> GeneralRetry(onRetryClick = { viewModel.onEvent(OnRetryClick) })
        }
    }
}

@Composable
private fun SeatReservationTimelineScreenContent(
    uiState: Content,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        InventoryDropDownPicker(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(MR.colors.surface.resourceId)),
            menuItems = uiState.availableOffices,
            selectedItem = uiState.selectedOffice,
            trailingIcon = MR.images.drop_down_icon,
            onItemClick = { office -> office?.let { onEvent(OfficeChanged(it)) } },
        ) { uiItem -> uiItem?.let { OfficeUiItem(it) } }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(
                items = uiState.reservableDateUi,
                key = { item ->
                    when (item) {
                        is Weekday -> item.date
                        is Weekend -> item.date
                        ReservableDateUiItem.Loading -> "Loading"
                        ReservableDateUiItem.Retry -> "Retry"
                    }
                },
            ) { item ->
                ReservableDateUiItem(
                    state = item,
                    onRetry = { onEvent(OnRetryClick) },
                    onOptionClick = { onEvent(OnOptionClick(option = it)) },
                )
            }
        }
    }
}
