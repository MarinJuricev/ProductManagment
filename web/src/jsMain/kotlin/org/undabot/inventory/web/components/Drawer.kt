package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

@Composable
fun Drawer(
    id: String,
    modifier: Modifier = Modifier,
    allowScrolling: Boolean = false,
    disableBackdrop: Boolean = false,
    closableOutside: Boolean = true,
    width: CSSLengthOrPercentageNumericValue = 300.px,
    placement: DrawerPlacement = DrawerPlacement.START,
    header: @Composable (() -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    Div(
        attrs = modifier
            .id(id)
            .classNames("offcanvas", placement.value)
            .styleModifier {
                property("--bs-offcanvas-width", width)
            }
            .toAttrs {
                attr("tabindex", "-1")
                attr("aria-labelledby", "offcanvasLabel")
                attr("aria-controls", "#$id")
                if (allowScrolling) attr("data-bs-scroll", "true")
                if (disableBackdrop) attr("data-bs-backdrop", "false")
                if (!closableOutside) attr("data-bs-backdrop", "static")
            },
    ) {
        header?.let {
            Div(
                attrs = Modifier
                    .classNames("offcanvas-header")
                    .toAttrs(),
            ) {
                it()
            }
        }

        Div(
            attrs = Modifier
                .classNames("offcanvas-body")
                .attrsModifier {
                    style {
                        property("--bs-offcanvas-padding-x", "0rem")
                        property("--bs-offcanvas-padding-y", "0rem")
                    }
                }
                .toAttrs(),
        ) {
            body()
        }
    }
}

fun Modifier.showDrawerOnClick(id: String): Modifier = attrsModifier {
    attr("data-bs-toggle", "offcanvas")
    attr("data-bs-target", "#$id")
}

fun Modifier.hideDrawerOnClick(): Modifier = attrsModifier {
    attr("data-bs-dismiss", "offcanvas")
}

enum class DrawerPlacement(val value: String) {
    TOP(value = "offcanvas-top"),
    BOTTOM(value = "offcanvas-bottom"),
    START(value = "offcanvas-start"),
    END(value = "offcanvas-end"),
}
