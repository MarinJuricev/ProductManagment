package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR
import parking.reservation.model.DateWithTimestamp
import utils.clickable

@Composable
fun DateChip(
    modifier: Modifier = Modifier,
    dateWithTimestamp: DateWithTimestamp,
    onRemoveClick: (date: Long) -> Unit = {},
) {
    BadgedBox(
        modifier = modifier.padding(10.dp),
        content = {
            Column(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BodySmallText(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = colorResource(MR.colors.secondary.resourceId),
                            shape = RoundedCornerShape(23.dp),
                        )
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    text = dateWithTimestamp.date,
                    color = MR.colors.secondary,
                    fontWeight = SemiBold,
                )
            }
        },
        badge = {
            Image(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .clip(CircleShape)
                    .clickable(
                        rippleEffectVisible = false,
                        onClick = { onRemoveClick(dateWithTimestamp.timestamp) },
                    ),
                imageType = ImageType.Resource(MR.images.close_round),
            )
        },
    )
}
