package seat.management.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType
import components.InventoryTextField
import org.product.inventory.shared.MR

@Composable
fun OfficeInput(
    modifier: Modifier = Modifier,
    officeName: String,
    seatsAmount: String,
    officeNamePlaceholder: String,
    seatsPlaceholder: String,
    onOfficeNameChanged: (name: String) -> Unit,
    onSeatsAmountChanged: (seats: String) -> Unit,
    onAddClick: (name: String, seats: String) -> Unit,
    enabled: Boolean,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InventoryTextField(
            modifier = Modifier.fillMaxWidth(0.6f),
            value = officeName,
            onValueChanged = onOfficeNameChanged,
            shape = RoundedCornerShape(12.dp),
            textPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
            placeholder = {
                BodySmallText(text = officeNamePlaceholder, color = MR.colors.textLight)
            },
        )

        InventoryTextField(
            modifier = Modifier.widthIn(max = 50.dp),
            value = seatsAmount,
            onValueChanged = onSeatsAmountChanged,
            shape = RoundedCornerShape(12.dp),
            textPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
            textAlign = TextAlign.Center,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            placeholder = {
                BodySmallText(text = seatsPlaceholder, color = MR.colors.textLight, textAlign = TextAlign.Center)
            },
        )

        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(19.dp)
                .alpha(if (enabled) 1f else 0.5f)
                .clickable(enabled = enabled) { onAddClick(officeName, seatsAmount) },
            imageType = ImageType.Resource(MR.images.ic_round_plus),
        )
    }
}
