package components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR

@Composable
fun CreateNewUserItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .clickable(onClick = onClick)
            .background(colorResource(MR.colors.surface.resourceId))
            .border(
                width = 1.dp,
                color = colorResource(MR.colors.textLight.resourceId),
                shape = RoundedCornerShape(23.dp),
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        BodyMediumText(
            text = stringResource(MR.strings.crew_management_create_new_user.resourceId),
            fontWeight = FontWeight.SemiBold,
        )
    }
}
