package parking.usersRequests.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType
import org.product.inventory.shared.MR

@Composable
fun NoFreeSpacesError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(MR.colors.warning.resourceId).copy(alpha = 0.08f))
            .border(
                shape = RoundedCornerShape(12.dp),
                color = colorResource(MR.colors.warning.resourceId).copy(alpha = 0.08f),
                width = 1.dp,
            )
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(imageType = ImageType.Resource(MR.images.warning_triangle))
        Spacer(modifier = Modifier.width(8.dp))
        BodySmallText(
            text = message,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.warning,
        )
    }
}
