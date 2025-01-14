package org.product.inventory.web.components

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.colors.ColorScheme

fun provideCustomColorScheme(
    _50: Color,
    _100: Color,
    _200: Color,
    _300: Color,
    _400: Color,
    _500: Color,
    _600: Color,
    _700: Color,
    _800: Color,
    _900: Color,
) = object : ColorScheme {
    override val _50: Color
        get() = _50
    override val _100: Color
        get() = _100
    override val _200: Color
        get() = _200
    override val _300: Color
        get() = _300
    override val _400: Color
        get() = _400
    override val _500: Color
        get() = _500
    override val _600: Color
        get() = _600
    override val _700: Color
        get() = _700
    override val _800: Color
        get() = _800
    override val _900: Color
        get() = _900
}

fun provideCustomColorScheme(color: Color) = provideCustomColorScheme(
    _50 = color,
    _100 = color,
    _200 = color,
    _300 = color,
    _400 = color,
    _500 = color,
    _600 = color,
    _700 = color,
    _800 = color,
    _900 = color,
)

fun Color.toColorScheme() = provideCustomColorScheme(this)
