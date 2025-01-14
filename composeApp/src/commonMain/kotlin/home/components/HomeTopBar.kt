package home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.Image
import components.ImageType.Resource
import components.ProfileHeadAndName
import org.product.inventory.SurfaceColor
import org.product.inventory.shared.MR
import user.model.InventoryAppRole
import user.model.InventoryAppUser

@Composable
fun HomeTopBar(
    user: InventoryAppUser,
    onMenuClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(SurfaceColor)
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ProfileHeadAndName(
            user = user,
            onLogoutClick = onLogoutClick,
        )
        Image(
            imageType = Resource(MR.images.dots),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                .clickable(onClick = onMenuClick),
        )
    }
}

@Composable
fun PreviewDashboardTopBar() {
    HomeTopBar(
        user = InventoryAppUser(
            email = "firstName.lastName",
            profileImageUrl = "",
            role = InventoryAppRole.Manager,
            id = "",
            hasPermanentGarageAccess = false,
        ),
        onMenuClick = {},
        onLogoutClick = {},
    )
}
