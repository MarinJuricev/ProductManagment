package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.overflowY
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.maxHeight
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.graphics.Image
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Ul
import org.product.inventory.web.core.ImageRes

enum class PopupDirection(val value: String? = null) {
    Bottom,
    Top(value = "dropup"),
    Left(value = "dropstart"),
    Right(value = "dropend"),
}

@Composable
fun <T> PopUpMenu(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemClickColor: String = HOVER_COLOR,
    direction: PopupDirection = PopupDirection.Bottom,
    maxHeight: CSSLengthOrPercentageNumericValue? = 300.px,
    dropdownMenuMinWidth: CSSLengthOrPercentageNumericValue? = null,
    itemContent: @Composable (Int, T, Modifier) -> Unit,
    toggleContent: @Composable () -> Unit = { DefaultPopupToggle() },
) {
    Div(
        attrs = modifier
            .classNames("dropdown")
            .thenIf(
                condition = direction != PopupDirection.Bottom,
                other = Modifier.classNames(direction.value.toString()),
            )
            .toAttrs(),
    ) {
        toggleContent()

        Ul(
            attrs = Modifier
                .fillMaxWidth()
                .classNames("dropdown-menu")
                .thenIfNotNull(dropdownMenuMinWidth) { minWidth ->
                    styleModifier { property("--bs-dropdown-min-width", minWidth) }
                }
                .thenIfNotNull(maxHeight) { height ->
                    maxHeight(height).styleModifier { overflowY(Overflow.Auto) }
                }
                .toAttrs(),
        ) {
            items.forEachIndexed { index, item ->
                itemContent(
                    index,
                    item,
                    Modifier
                        .classNames("dropdown-item")
                        .listItemClickColor(itemClickColor),
                )
            }
        }
    }
}

@Composable
private fun DefaultPopupToggle() {
    Image(
        modifier = Modifier
            .attrsModifier { attr("data-bs-toggle", "dropdown") }
            .pointerCursor(),
        src = ImageRes.options,
    )
}

private const val HOVER_COLOR = "#f8f9fa"
