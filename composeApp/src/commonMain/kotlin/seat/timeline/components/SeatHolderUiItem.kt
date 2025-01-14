package seat.timeline.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType
import org.product.inventory.SecondaryColor
import org.product.inventory.shared.MR

@Composable
fun SeatHolderUiItem(
    imageUrl: String,
    username: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(50))
            .border(
                width = 1.dp,
                color = colorResource(MR.colors.textLight.resourceId),
                shape = RoundedCornerShape(50),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Image(
            imageType = ImageType.Remote(imageUrl),
            modifier = modifier
                .padding(5.dp)
                .clip(CircleShape)
                .size(24.dp)
                .border(width = 1.dp, shape = CircleShape, color = SecondaryColor),
        )
        BodySmallText(text = username, modifier = Modifier.padding(end = 10.dp))
    }
}
