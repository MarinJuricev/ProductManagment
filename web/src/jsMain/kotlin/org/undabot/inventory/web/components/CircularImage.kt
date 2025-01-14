package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.px

@Composable
fun CircularImage(
    path: String,
    modifier: Modifier = Modifier,
    size: CSSLengthOrPercentageNumericValue = 60.px,
    radius: Int = 60,
) {
    Image(
        src = path,
        modifier = modifier
            .size(size = size)
            .clip(Circle(radius = radius)),
    )
}
