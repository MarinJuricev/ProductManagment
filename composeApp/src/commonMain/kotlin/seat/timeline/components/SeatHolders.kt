package seat.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import seat.timeline.model.ReservableDateUiItem
import user.model.getUsername

@Composable
fun SeatHolders(
    modifier: Modifier = Modifier,
    state: ReservableDateUiItem.Weekday,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RemainingSeats(count = state.remainingSeats, text = state.remainingSeatsText)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            items(items = state.seatHolders, key = { it.id }) {
                SeatHolderUiItem(imageUrl = it.profileImageUrl, username = it.getUsername())
            }
        }
    }
}
