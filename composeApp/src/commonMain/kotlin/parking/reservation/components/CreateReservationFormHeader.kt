package parking.reservation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import components.BodySmallText
import components.ProfileHeadAndName
import user.model.InventoryAppUser

@Composable
internal fun CreateReservationFormHeader(
    title: String,
    user: InventoryAppUser,
    modifier: Modifier = Modifier,
    onUserSelectClick: () -> Unit = {},
    enableUserSelect: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BodySmallText(text = title, fontWeight = FontWeight.SemiBold)
        ProfileHeadAndName(
            user = user,
            onUserSelectClick = onUserSelectClick,
            enableUserSelect = enableUserSelect,
        )
    }
}
