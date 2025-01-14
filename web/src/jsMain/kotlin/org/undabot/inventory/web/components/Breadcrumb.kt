package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Ol
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

data class BreadcrumbItem(
    val text: String,
    val route: String,
    val isNavigable: Boolean = false,
)

@Composable
fun Breadcrumb(
    items: List<BreadcrumbItem>,
    modifier: Modifier = Modifier,
    divider: String = "/",
    onClick: (String) -> Unit,
) {
    Nav(
        attrs = modifier
            .toAttrs {
                attr("aria-label", "breadcrumb")
                attr("style", "--bs-breadcrumb-divider: '$divider';")
            },
    ) {
        Ol(
            attrs = Modifier
                .classNames("breadcrumb")
                .toAttrs(),
        ) {
            items.forEachIndexed { index, item ->
                Li(
                    attrs = Modifier
                        .classNames("breadcrumb-item")
                        .thenIf(
                            condition = !item.isNavigable,
                            other = Modifier.classNames("active"),
                        )
                        .toAttrs {
                            if (!item.isNavigable) attr("aria-current", "page")
                        },
                ) {
                    val isLastItem = index == items.lastIndex

                    when {
                        isLastItem -> Text(
                            value = item.text,
                            modifier = Modifier
                                .color(color = MR.colors.textBlack.toCssColor())
                                .fontWeight(FontWeight.SemiBold),
                            size = 14.px,
                        )

                        item.isNavigable -> Text(
                            value = item.text,
                            modifier = Modifier
                                .color(color = MR.colors.textBlack.toCssColor())
                                .textDecorationLine(TextDecorationLine.Underline)
                                .clickable { onClick(item.route) },
                            size = 14.px,
                        )

                        else -> Text(
                            value = item.text,
                            modifier = Modifier.color(color = MR.colors.textBlack.toCssColor()),
                            size = 14.px,
                        )
                    }
                }
            }
        }
    }
}
