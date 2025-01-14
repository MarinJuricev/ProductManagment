package org.product.inventory.web.components

import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.disabled
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.px

fun Modifier.pointerCursor() = cursor(Cursor.Pointer)
fun Modifier.defaultCursor() = cursor(Cursor.Default)
fun Modifier.enabledIf(isEnabled: Boolean) = thenIf(
    condition = !isEnabled,
    other = Modifier.disabled(),
)

fun <T : Any?> Modifier.thenIfNotNull(
    value: T?,
    action: Modifier.(T) -> Modifier,
) = run {
    if (value != null) {
        action(value)
    } else {
        this
    }
}

fun Modifier.clickable(onClick: () -> Unit) =
    pointerCursor().onClick { onClick() }

fun <T : Any> Modifier.ifNotNull(
    value: T?,
    action: Modifier.(T) -> Modifier,
) = when (value) {
    null -> this
    else -> action(value)
}

fun Modifier.roundedCornersShape(
    all: CSSLengthOrPercentageNumericValue,
) = styleModifier { property("border-radius", all) }

fun Modifier.roundedCornersShape(
    topLeft: CSSLengthOrPercentageNumericValue = 0.px,
    topRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottomRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottomLeft: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    property("border-top-left-radius", topLeft)
    property("border-top-right-radius", topRight)
    property("border-bottom-right-radius", bottomRight)
    property("border-bottom-left-radius", bottomLeft)
}

fun Modifier.spaceBetween() = attrsModifier {
    style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        alignItems(AlignItems.Center)
    }
}

// should be used with A element
fun Modifier.listItemClickColor(color: String) = attrsModifier {
    style {
        property("--bs-dropdown-link-active-bg", color)
    }
}

// used to limit the number of lines in a text element
fun Modifier.maxLines(
    lines: Int,
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
): Modifier = textOverflow(textOverflow)
    .overflow(Overflow.Hidden)
    .styleModifier {
        property("display", "-webkit-box")
        property("-webkit-line-clamp", lines.toString())
        property("line-clamp", lines.toString())
        property("-webkit-box-orient", "vertical")
    }

val fullSizeVisibleModifier = Modifier
    .display(DisplayStyle.Flex)
    .fillMaxWidth()
    .fillMaxHeight()

val invisibleModifier = Modifier.display(DisplayStyle.None)
