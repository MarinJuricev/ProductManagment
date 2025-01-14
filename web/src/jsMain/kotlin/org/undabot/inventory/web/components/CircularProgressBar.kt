package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

@Composable
fun CircularProgressBar(
    modifier: Modifier = Modifier,
    color: Color.Rgb = MR.colors.secondary.toCssColor(),
    width: CSSSizeValue<CSSUnit.em> = 0.2.em,
) {
    Div(
        attrs = modifier
            .classNames("spinner-border")
            .toAttrs {
                attr("role", "status")
                style {
                    color(color)
                    property("--bs-spinner-border-width", width.toString())
                }
            },
    ) {
        Span(attrs = Modifier.classNames("visually-hidden").toAttrs()) {
            Text(value = "Loading...")
        }
    }
}
