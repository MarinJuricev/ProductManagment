package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR

@Composable
fun SingleLineFormValue(
    modifier: Modifier = Modifier,
    valueName: String,
    value: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        BodySmallText(
            text = valueName,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        BodySmallText(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.textBlack,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
