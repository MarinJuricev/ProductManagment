package seat.timeline.components

import androidx.compose.runtime.Composable
import components.GeneralRetry
import components.LoadingIndicator
import seat.timeline.model.ReservableDateUiItem
import seat.timeline.model.ReservableDateUiItem.Loading
import seat.timeline.model.ReservableDateUiItem.Retry
import seat.timeline.model.ReservableDateUiItem.Weekday
import seat.timeline.model.ReservableDateUiItem.Weekend
import seat.timeline.model.ReservableDateUiItemOption

@Composable
fun ReservableDateUiItem(
    state: ReservableDateUiItem,
    onRetry: () -> Unit,
    onOptionClick: (option: ReservableDateUiItemOption) -> Unit,
) {
    when (state) {
        is Loading -> LoadingIndicator()
        is Retry -> GeneralRetry(onRetry)
        is Weekday -> DayItem(state = state, onOptionClick = onOptionClick)
        is Weekend -> WeekendItem(state)
    }
}
