package seat.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import seat.timeline.model.ReservableDateUiItem

@Composable
fun WeekendItem(
    state: ReservableDateUiItem.Weekend,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DayItemHeaderDate(date = state.date, isWeekend = true)
        WeekendDayItemBody()
    }
}
