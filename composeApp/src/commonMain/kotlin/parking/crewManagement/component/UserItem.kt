package parking.crewManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import components.ProfileHeadAndName
import org.product.inventory.shared.MR
import user.model.InventoryAppUser

@Composable
fun UserItem(
    user: InventoryAppUser,
    modifier: Modifier = Modifier,
    onUserClick: (InventoryAppUser) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .clickable { onUserClick(user) }
            .background(colorResource(MR.colors.surface.resourceId))
            .border(
                width = 1.dp,
                color = colorResource(MR.colors.textLight.resourceId),
                shape = RoundedCornerShape(23.dp),
            )
            .padding(vertical = 16.dp),
    ) {
        ProfileHeadAndName(user = user, showRole = false)
    }
}
