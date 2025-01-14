package seat.timeline.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodyMediumText
import org.product.inventory.shared.MR

@Composable
fun RemainingSeats(
    count: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        BodyMediumText(
            text = count,
            color = MR.colors.secondary,
            fontWeight = FontWeight.SemiBold,
        )

        BodyMediumText(
            text = text,
            color = MR.colors.textBlack,
        )
    }
}
