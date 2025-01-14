package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR

@Composable
fun TextWithBorderForm(
    formTitle: String,
    formValue: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodySmallText(formTitle, fontWeight = FontWeight.SemiBold, color = MR.colors.secondary)
        BodySmallText(
            modifier = Modifier
                .border(width = 1.dp, color = colorResource(MR.colors.textLight.resourceId), shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 36.dp, vertical = 12.dp),
            text = formValue,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.textBlack,
        )
    }
}
