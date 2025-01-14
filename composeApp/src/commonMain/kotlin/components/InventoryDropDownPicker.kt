package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource
import org.product.inventory.shared.MR
import utils.clickable

@Composable
fun <ItemType : Any> InventoryDropDownPicker(
    modifier: Modifier = Modifier,
    innerContentModifier: Modifier = modifier,
    enabled: Boolean = true,
    menuItems: List<ItemType?>,
    selectedItem: ItemType?,
    placeholder: @Composable (() -> Unit) = {},
    onItemClick: (ItemType?) -> Unit = {},
    trailingIcon: ImageResource? = null,
    borderColor: ColorResource = MR.colors.textLight,
    composeItem: @Composable (item: ItemType?) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(rippleEffectVisible = enabled) { if (enabled) isExpanded = true }
                .border(
                    width = 1.dp,
                    color = colorResource(borderColor.resourceId),
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            selectedItem?.let { composeItem(it) } ?: placeholder()
            trailingIcon?.let {
                Image(
                    modifier = Modifier.padding(start = 23.dp),
                    imageType = ImageType.Resource(trailingIcon),
                )
            }
        }
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(background = Color.Transparent),
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp)),
        ) {
            DropdownMenu(
                modifier = innerContentModifier
                    .background(colorResource(MR.colors.surface.resourceId)),
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    menuItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .clickable {
                                    isExpanded = false
                                    onItemClick(item)
                                }
                                .padding(horizontal = 28.dp, vertical = 10.dp),
                        ) {
                            composeItem(item)
                        }
                    }
                }
            }
        }
    }
}
