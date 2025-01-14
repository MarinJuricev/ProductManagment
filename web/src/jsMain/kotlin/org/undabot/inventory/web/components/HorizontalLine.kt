package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

@Composable
fun HorizontalLine(
    modifier: Modifier = Modifier,
    width: CSSLengthOrPercentageNumericValue = 100.percent,
    height: CSSLengthOrPercentageNumericValue = 2.px,
    color: CSSColorValue = Colors.Black,
) {
    Div(
        attrs = modifier
            .height(height)
            .width(width)
            .background(color = color)
            .toAttrs(),
    )
}
