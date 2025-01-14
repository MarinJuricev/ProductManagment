package seat.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodyMediumText
import components.Image
import components.ImageType
import org.product.inventory.shared.MR

@Composable
fun WeekendDayItemBody(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(23.dp))
            .background(colorResource(MR.colors.surface.resourceId))
            .border(
                width = 1.dp,
                color = colorResource(MR.colors.textLight.resourceId),
                shape = RoundedCornerShape(23.dp),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Inside,
                imageType = ImageType.Resource(MR.images.weekend_icon),
            )
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .background(colorResource(MR.colors.disabled.resourceId)),
        ) {
            BodyMediumText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(MR.strings.seat_reservation_weekend.resourceId),
                fontWeight = FontWeight.SemiBold,
                color = MR.colors.textLight,
            )
        }
    }
}
