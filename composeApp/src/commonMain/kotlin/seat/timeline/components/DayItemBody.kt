package seat.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR
import seat.timeline.model.ReservableDateUiItem
import seat.timeline.model.ReservableDateUiItemOption

@Composable
fun DayItemBody(
    state: ReservableDateUiItem.Weekday,
    onOptionClick: (ReservableDateUiItemOption) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(23.dp))
            .background(colorResource(MR.colors.surface.resourceId))
            .border(
                width = 1.dp,
                color = colorResource(MR.colors.textLight.resourceId),
                shape = RoundedCornerShape(23.dp),
            ),
    ) {
        SeatHolders(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            state = state,
        )
        DayOptionButton(
            option = state.option,
            onOptionClick = onOptionClick,
        )
    }
}
