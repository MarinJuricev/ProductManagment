package org.product.inventory.web

import com.varabyte.kobweb.compose.ui.graphics.Color
import dev.icerock.moko.resources.ColorResource
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb

fun ColorResource.toCssColor(
    lightMode: Boolean = true,
) = when (lightMode) {
    true -> Color.rgba(value = lightColor.rgba)
    false -> Color.rgba(value = darkColor.rgba)
}

fun String.toRgb(): CSSColorValue = rgb(
    r = substring(0, 2).toInt(radix = 16),
    g = substring(2, 4).toInt(radix = 16),
    b = substring(4, 6).toInt(radix = 16),
)
