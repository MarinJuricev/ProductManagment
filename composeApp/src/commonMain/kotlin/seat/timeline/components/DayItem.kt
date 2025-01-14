package seat.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import seat.timeline.model.ReservableDateUiItem.Weekday
import seat.timeline.model.ReservableDateUiItemOption

@Composable
fun DayItem(
    state: Weekday,
    modifier: Modifier = Modifier,
    onOptionClick: (ReservableDateUiItemOption) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DayItemHeaderDate(date = state.date)
        DayItemBody(state = state, onOptionClick = onOptionClick)
    }
}
