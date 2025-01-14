package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR
import utils.clickable

@Composable
fun TextChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    BodySmallText(
        modifier = modifier
            .clip(RoundedCornerShape(23.dp))
            .clickable(onClick = onClick)
            .background(
                color = colorResource(MR.colors.secondary.resourceId),
                shape = RoundedCornerShape(23.dp),
            )
            .padding(vertical = 8.dp, horizontal = 10.dp),
        text = text,
        color = MR.colors.surface,
        fontWeight = SemiBold,
    )
}
