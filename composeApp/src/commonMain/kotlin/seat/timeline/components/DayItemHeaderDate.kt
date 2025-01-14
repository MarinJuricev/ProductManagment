package seat.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodyLargeText
import org.product.inventory.shared.MR

@Composable
fun DayItemHeaderDate(
    date: String,
    isWeekend: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.58f)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(colorResource(if (isWeekend) MR.colors.disabled.resourceId else MR.colors.secondary.resourceId))
            .padding(vertical = 12.dp),
    ) {
        BodyLargeText(
            modifier = Modifier.align(Alignment.Center),
            text = date,
            color = if (isWeekend) MR.colors.textLight else MR.colors.surface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
