package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import dev.icerock.moko.resources.ImageResource
import org.product.inventory.shared.MR

@Composable
fun <ItemType : Any> DropdownPickerForm(
    formTitle: String,
    menuItems: List<ItemType>,
    selectedItem: ItemType?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: ImageResource = MR.images.drop_down_icon,
    onItemClick: (ItemType?) -> Unit = {},
    composeItem: @Composable (item: ItemType?) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BodySmallText(
            text = formTitle,
            fontWeight = SemiBold,
            color = MR.colors.secondary,
        )
        InventoryDropDownPicker(
            menuItems = menuItems,
            selectedItem = selectedItem,
            trailingIcon = trailingIcon,
            onItemClick = onItemClick,
            enabled = enabled,
        ) { composeItem(it) }
    }
}
