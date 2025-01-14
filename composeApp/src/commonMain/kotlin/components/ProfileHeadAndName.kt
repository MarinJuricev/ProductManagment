package components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.product.inventory.SecondaryColor
import org.product.inventory.shared.MR
import user.model.InventoryAppUser
import user.model.getUsername

@Composable
fun ProfileHeadAndName(
    user: InventoryAppUser,
    modifier: Modifier = Modifier,
    showRole: Boolean = true,
    enableUserSelect: Boolean = false,
    onLogoutClick: () -> Unit = {},
    onUserSelectClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(enabled = enableUserSelect, onClick = onUserSelectClick),
    ) {
        Image(
            imageType = ImageType.Remote(user.profileImageUrl),
            modifier = modifier.clip(CircleShape).size(32.dp)
                .border(width = 2.dp, shape = CircleShape, color = SecondaryColor)
                .clickable(enabled = !enableUserSelect, onClick = onLogoutClick),
        )
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            BodySmallText(text = user.getUsername())
            if (showRole) {
                BodySmallText(
                    text = user.role.toString(),
                    color = MR.colors.secondary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (enableUserSelect) {
            Image(imageType = ImageType.Resource(MR.images.ic_edit_user))
        }
    }
}
