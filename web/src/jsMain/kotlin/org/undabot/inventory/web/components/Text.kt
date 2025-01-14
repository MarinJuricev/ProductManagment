package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.Resize
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.css.overflow
import com.varabyte.kobweb.compose.css.textOverflow
import com.varabyte.kobweb.compose.css.whiteSpace
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.resize
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.product.inventory.web.core.FontRes

@Composable
fun Text(
    value: String,
    modifier: Modifier = Modifier.color(Colors.Black),
    size: CSSLengthOrPercentageNumericValue? = null,
    fontFamily: String? = FontRes.Montserrat,
) {
    SpanText(
        text = value,
        modifier = modifier
            .ifNotNull(size) { fontSize(it) }
            .ifNotNull(fontFamily) { fontFamily(it) },
    )
}

@Composable
fun OneLineText(
    value: String,
    modifier: Modifier = Modifier.color(Colors.Black),
    size: CSSLengthOrPercentageNumericValue? = null,
    fontFamily: String? = FontRes.Montserrat,
) {
    Text(
        value = value,
        size = size,
        fontFamily = fontFamily,
        modifier = modifier
            .attrsModifier {
                attr("overflow", "hidden")
                attr("white-space", "nowrap")
            },
    )
}

@Composable
fun TextArea(
    value: String,
    modifier: Modifier = Modifier,
    rowsCount: Int = 5,
    enabled: Boolean = true,
    placeholder: String = "",
    resize: Resize = if (enabled) Resize.Vertical else Resize.None,
    onValueChange: (String) -> Unit = {},
) {
    org.jetbrains.compose.web.dom.TextArea(
        value = value,
        attrs = modifier
            .enabledIf(enabled)
            .classNames("form-control")
            .resize(resize)
            .toAttrs {
                onInput { onValueChange(it.value) }
                attr("rows", rowsCount.toString())
                placeholder(placeholder)
            },
    )
}

@Composable
fun TextWithOverflow(
    value: String,
    modifier: Modifier = Modifier,
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
    size: CSSLengthOrPercentageNumericValue? = null,
    fontFamily: String? = FontRes.Montserrat,
) {
    Text(
        modifier = modifier.styleModifier {
            width(100.percent)
            whiteSpace(WhiteSpace.NoWrap)
            overflow(Overflow.Hidden)
            textOverflow(textOverflow)
        },
        value = value,
        size = size,
        fontFamily = fontFamily,
    )
}
