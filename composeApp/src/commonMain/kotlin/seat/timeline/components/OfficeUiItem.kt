package seat.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.LoadingIndicator
import org.product.inventory.shared.MR
import seat.timeline.interaction.OfficeItem
import seat.timeline.interaction.OfficeItem.OfficeData

@Composable
fun OfficeUiItem(
    state: OfficeItem,
) {
    when (state) {
        is OfficeItem.Loading -> LoadingIndicator(
            modifier = Modifier
                .padding(horizontal = 50.dp, vertical = 8.dp)
                .size(20.dp),
        )

        is OfficeData -> BodySmallText(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(120.dp)
                .background(color = colorResource(MR.colors.surface.resourceId)),
            text = state.office.title,
            color = MR.colors.secondary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )

        is OfficeItem.Undefined -> BodySmallText(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(120.dp)
                .background(color = colorResource(MR.colors.surface.resourceId)),
            text = "-",
            color = MR.colors.secondary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}
