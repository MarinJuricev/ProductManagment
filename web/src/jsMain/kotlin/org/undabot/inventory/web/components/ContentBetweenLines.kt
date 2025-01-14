package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.px

@Composable
fun ContentBetweenLines(
    modifier: Modifier = Modifier,
    lineColor: CSSColorValue = Colors.Black,
    lineHeight: CSSLengthOrPercentageNumericValue = 2.px,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        HorizontalLine(
            color = lineColor,
            height = lineHeight,
        )

        content()
    }
}
