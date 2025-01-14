package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.product.inventory.BackgroundColor
import org.product.inventory.SurfaceColor
import org.product.inventory.shared.MR

@Composable
fun TextualDivider(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BackgroundColor)
                .align(Alignment.Center),
        )
        BodySmallText(
            text,
            color = MR.colors.textLight,
            modifier = Modifier
                .align(Alignment.Center)
                .background(SurfaceColor)
                .padding(horizontal = 8.dp),
        )
    }
}
