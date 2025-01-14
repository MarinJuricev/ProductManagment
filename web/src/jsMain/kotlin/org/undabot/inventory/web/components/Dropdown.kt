package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import kotlinx.browser.document
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Li

@Composable
fun <T> Dropdown(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemSpacing: CSSLengthOrPercentageNumericValue = 10.px,
    itemClickColor: String = HOVER_COLOR,
    enabled: Boolean = true,
    maxHeight: CSSLengthOrPercentageNumericValue? = 300.px,
    direction: PopupDirection = PopupDirection.Bottom,
    dropdownMenuMinWidth: CSSLengthOrPercentageNumericValue? = null,
    onItemSelect: (Int, T) -> Unit,
    defaultContent: @Composable () -> Unit,
    itemContent: @Composable (T, Modifier) -> Unit,
) {
    PopUpMenu(
        items = items,
        modifier = modifier,
        itemClickColor = itemClickColor,
        direction = direction,
        maxHeight = maxHeight,
        dropdownMenuMinWidth = dropdownMenuMinWidth,
        toggleContent = {
            Box(
                modifier = Modifier
                    .thenIf(
                        condition = enabled,
                        other = Modifier
                            .classNames("dropdown-toggle")
                            .attrsModifier {
                                style { color(Colors.Transparent) }
                                attr("data-bs-toggle", "dropdown")
                            }
                            .pointerCursor(),
                    ),
            ) {
                defaultContent()
            }
        },
        itemContent = { index, item, _ ->
            Li(
                attrs = Modifier
                    .fillMaxWidth()
                    .toAttrs(),
            ) {
                A(
                    attrs = Modifier
                        .fillMaxWidth()
                        .classNames("dropdown-item")
                        .clickable { onItemSelect(index, item) }
                        .listItemClickColor(itemClickColor)
                        .toAttrs(),
                ) {
                    itemContent(
                        item,
                        Modifier.fillMaxWidth().clickable {
                            onItemSelect(index, item)
                            // workaround for the dropdown not closing after clicking an item
                            fakeClick()
                        },
                    )
                }
            }

            if (index != items.lastIndex) {
                Box(modifier = Modifier.fillMaxWidth().height(itemSpacing))
            }
        },
    )
}

private fun fakeClick() {
    document.createEvent("Event").apply {
        initEvent(type = "click", bubbles = false, cancelable = true)
        document.dispatchEvent(this)
    }
}

private const val HOVER_COLOR = "#f8f9fa"
