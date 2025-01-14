package seat.management.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import org.product.inventory.shared.MR

@Composable
fun SeatManagementFormHeader(
    officeNameLabel: String,
    seatsAmountLabel: String,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        BodySmallText(
            modifier = Modifier.fillMaxWidth(0.6f),
            text = officeNameLabel,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        BodySmallText(
            modifier = Modifier.width(50.dp),
            text = seatsAmountLabel,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        Spacer(Modifier.size(19.dp))
    }
}
