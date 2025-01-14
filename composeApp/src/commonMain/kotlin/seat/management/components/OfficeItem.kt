package seat.management.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType
import components.InventoryDropDownPicker
import components.InventoryTextField
import org.product.inventory.shared.MR
import seatreservation.model.Office

@Composable
fun OfficeItem(
    office: Office,
    availableOptions: List<OfficeItemOption>,
    itemMenuIconResource: ImageType.Resource,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onOfficeOptionClick: (OfficeItemOption, office: Office) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InventoryTextField(
            modifier = Modifier.fillMaxWidth(0.6f),
            enabled = enabled,
            value = office.title,
            onValueChanged = {},
            shape = RoundedCornerShape(12.dp),
            textPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        )

        InventoryTextField(
            modifier = Modifier.widthIn(max = 50.dp),
            enabled = enabled,
            value = office.numberOfSeats.toString(),
            onValueChanged = {},
            shape = RoundedCornerShape(12.dp),
            textPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
            textAlign = TextAlign.Center,
        )

        InventoryDropDownPicker(
            menuItems = availableOptions,
            selectedItem = null,
            placeholder = { Image(itemMenuIconResource) },
            borderColor = MR.colors.surface,
            onItemClick = { item -> item?.let { onOfficeOptionClick(item, office) } },

        ) { item ->
            item?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),

                ) {
                    Image(modifier = Modifier.size(16.dp), imageType = it.iconResource)
                    if (it.displayedText.isNotEmpty()) {
                        BodySmallText(it.displayedText)
                    }
                }
            }
        }
    }
}

sealed interface OfficeItemOption {
    val displayedText: String
    val iconResource: ImageType.Resource

    data class Delete(
        override val displayedText: String,
        override val iconResource: ImageType.Resource,
    ) : OfficeItemOption

    data class Edit(
        override val displayedText: String,
        override val iconResource: ImageType.Resource,
    ) : OfficeItemOption
}
