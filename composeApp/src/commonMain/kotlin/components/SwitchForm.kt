package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import org.product.inventory.shared.MR

@Composable
fun SwitchForm(
    formTitle: String,
    switchActive: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BodySmallText(
            text = formTitle,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        Switch(
            checked = switchActive,
            colors = SwitchDefaults.colors(
                checkedTrackColor = colorResource(MR.colors.secondary.resourceId),
                checkedBorderColor = Color.Black.copy(alpha = 0.1f),
                uncheckedTrackColor = colorResource(MR.colors.textLight.resourceId),
                uncheckedBorderColor = colorResource(MR.colors.textBlack.resourceId),
            ),
            onCheckedChange = onCheckedChange,
        )
    }
}
