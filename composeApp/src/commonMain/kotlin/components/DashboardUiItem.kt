package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.model.DashboardOption
import dev.icerock.moko.resources.ImageResource
import org.product.inventory.shared.MR

@Composable
fun DashboardUiItem(
    item: DashboardOption,
    onItemClick: (option: DashboardOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(23.dp))
            .border(
                shape = RoundedCornerShape(23.dp),
                width = 1.dp,
                color = colorResource(MR.colors.textLight.resourceId),
            )
            .background(colorResource(MR.colors.surface.resourceId))
            .clickable { onItemClick(item) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ParkingDashboardHeader(title = stringResource(item.title.resourceId), icon = item.icon)

        BodyMediumText(
            text = stringResource(item.description.resourceId),
            modifier = modifier.fillMaxWidth(0.9f).padding(top = 15.dp, bottom = 28.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ParkingDashboardHeader(
    title: String,
    icon: ImageResource,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 23.dp,
                        topEnd = 0.dp,
                        bottomEnd = 23.dp,
                        bottomStart = 0.dp,
                    ),
                )
                .background(colorResource(MR.colors.secondary.resourceId)),
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center).size(41.dp),
                imageType = ImageType.Resource(icon),
            )
        }
        TitleLargeText(
            text = title,
            modifier = Modifier.fillMaxWidth(0.9f),
            textAlign = TextAlign.Center,
        )
    }
}
