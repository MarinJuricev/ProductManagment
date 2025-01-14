package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthNumericValue
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.graphics.Image
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.toCssColor

@Composable
fun InfoMessage(
    message: String,
    modifier: Modifier = Modifier,
    backgroundColor: CSSColorValue = MR.colors.warning.toCssColor().copyf(alpha = 0.1f),
    cornerRadius: CSSLengthOrPercentageNumericValue = 12.px,
    borderColor: CSSColorValue = MR.colors.warning.toCssColor(),
    borderWidth: CSSLengthNumericValue = 1.px,
    textColor: CSSColorValue = MR.colors.warning.toCssColor(),
    textSize: CSSLengthOrPercentageNumericValue = 16.px,
    leadingIcon: @Composable () -> Unit = { DefaultLeadingIcon() },
) {
    Row(
        modifier = modifier
            .roundedCornersShape(cornerRadius)
            .padding(10.px)
            .border {
                color(borderColor)
                style(LineStyle.Solid)
                width(borderWidth)
            }
            .borderRadius(cornerRadius)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()

        Text(
            value = message,
            size = textSize,
            modifier = Modifier
                .margin(left = 10.px)
                .color(textColor)
                .fontWeight(FontWeight.SemiBold)
                .textAlign(TextAlign.Center),
        )
    }
}

@Composable
private fun DefaultLeadingIcon() {
    Image(src = ImageRes.warningTriangle)
}
