package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.graphics.Image
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.ParkingOption.Companion.INVALID_ID
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.toCssColor

data class ParkingOption(
    val value: String,
    val id: String,
) {
    companion object {
        const val INVALID_VALUE = "-"
        const val INVALID_ID = "-1"
        val EMPTY = ParkingOption(value = INVALID_VALUE, id = INVALID_ID)
    }
}

fun ParkingOption?.isValid() = orEmpty().id != INVALID_ID

fun ParkingOption?.orEmpty() = this ?: ParkingOption.EMPTY

@Composable
fun ParkingOptionsDropdown(
    entries: List<ParkingOption>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    defaultItem: ParkingOption = ParkingOption.EMPTY,
    onItemSelect: (ParkingOption) -> Unit,
) {
    Dropdown(
        modifier = modifier,
        items = entries,
        enabled = enabled,
        defaultContent = {
            Row(
                modifier = Modifier
                    .border {
                        color(MR.colors.textLight.toCssColor())
                        style(LineStyle.Solid)
                        width(1.px)
                    }
                    .borderRadius(12.px)
                    .padding(10.px),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(defaultItem.value)

                Image(
                    src = ImageRes.userRequestsStatusDropdown,
                    modifier = Modifier.margin(left = 20.px),
                )
            }
        },
        itemContent = { item, modifier ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                Text(value = item.value)
            }
        },
        onItemSelect = { _, item -> onItemSelect(item) },
    )
}
