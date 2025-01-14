package seat.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import components.BodyMediumText
import org.product.inventory.shared.MR
import seat.timeline.model.ReservableDateUiItemOption
import seat.timeline.model.ReservableDateUiItemOption.Cancel
import seat.timeline.model.ReservableDateUiItemOption.Reserve
import utils.clickable

@Composable
fun DayOptionButton(
    option: ReservableDateUiItemOption,
    modifier: Modifier = Modifier,
    onOptionClick: (ReservableDateUiItemOption) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                when (option) {
                    is Cancel -> colorResource(MR.colors.disabled.resourceId)
                    is Reserve -> if (option.enabled) {
                        colorResource(MR.colors.secondary.resourceId)
                    } else {
                        colorResource(
                            MR.colors.secondary.resourceId,
                        ).copy(alpha = 0.5f)
                    }
                },
            )
            .clickable { onOptionClick(option) },
    ) {
        BodyMediumText(
            modifier = Modifier.align(Alignment.Center),
            text = option.optionTitle,
            color = when (option) {
                is Cancel -> MR.colors.textBlack
                is Reserve -> if (option.enabled) MR.colors.surface else MR.colors.disabled
            },
            fontWeight = FontWeight.SemiBold,
        )
    }
}
