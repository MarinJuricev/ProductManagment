package parking.reservation.components.userpicker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType
import org.product.inventory.shared.MR
import utils.clickable

@Composable
fun UserItem(
    isSelected: Boolean,
    imageUrl: String,
    username: String,
    modifier: Modifier = Modifier,
    onUserClick: () -> Unit,
) {
    Row(
        modifier = modifier.clickable(rippleEffectVisible = false, onClick = onUserClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(isSelected) {
            Image(
                modifier = Modifier.size(16.dp),
                imageType = ImageType.Resource(MR.images.ic_tick),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier
                .size(40.dp)
                .clip(shape = CircleShape)
                .border(
                    width = 1.dp,
                    color = colorResource(MR.colors.secondary.resourceId),
                    shape = CircleShape,
                ),

            imageType = ImageType.Remote(imageUrl),
        )
        Spacer(modifier = Modifier.width(8.dp))
        BodySmallText(text = username)
    }
}
