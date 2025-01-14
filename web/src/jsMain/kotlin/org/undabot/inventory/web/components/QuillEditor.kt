package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.toAttrs
import kotlinx.browser.document
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.get

@Composable
fun QuillEditor(
    id: String,
    content: String,
    modifier: Modifier = Modifier,
    toolbarStyle: QuillToolbarStyle = QuillToolbarStyle(),
    containerStyle: QuillContainerStyle = QuillContainerStyle(),
    enabled: Boolean = true,
    onTextChanged: (String) -> Unit,
) {
    Div(
        attrs = modifier
            .id("editor-container")
            .overflow(Overflow.Hidden)
            .toAttrs(),
    ) {
        Div(
            attrs = Modifier
                .id("editor")
                .toAttrs(),
        ) {
            registerRefScope(
                ref(id) {
                    it.innerHTML = content
                },
            )
        }
    }

    DisposableEffect(toolbarStyle, containerStyle) {
        val style = provideQuillStyle(toolbarStyle, containerStyle)
        document.body?.append(style)

        onDispose {
            style.remove()
        }
    }

    DisposableEffect(id, enabled) {
        val quill: dynamic = createQuill(enabled)
        quill.on("text-change") { _: dynamic, _: dynamic, _: dynamic ->
            val text = quill.root.innerHTML as String
            onTextChanged(text)
        }

        onDispose {
            document.getElementsByClassName("ql-toolbar")[0]?.remove()
        }
    }
}

// since we cannot change options dynamically we need to create new instance each time with different options
// also js() does not support dynamic content so it is not possible to use enabled: Boolean in js() argument
private fun createQuill(
    enabled: Boolean,
): dynamic = when (enabled) {
    true -> js("new Quill('#editor', { readOnly: false, theme: 'snow', modules: { toolbar: [ [{ 'header': [1, 2, 3, 4, 5, 6, false] }], ['bold', 'italic', 'underline'], [{ 'align': [] }], ['clean'] ] } })")
    false -> js("new Quill('#editor', { readOnly: true, theme: 'snow', modules: { toolbar: [ [{ 'header': [1, 2, 3, 4, 5, 6, false] }], ['bold', 'italic', 'underline'], [{ 'align': [] }], ['clean'] ] } })")
}

private fun provideQuillStyle(
    toolbarStyle: QuillToolbarStyle,
    containerStyle: QuillContainerStyle,
): HTMLStyleElement = (document.createElement("style") as HTMLStyleElement)
    .apply {
        textContent = """
            .ql-toolbar {
                border: ${toolbarStyle.borderWidth} ${toolbarStyle.borderLineStyle} ${toolbarStyle.borderColor} !important;
                background-color: ${toolbarStyle.backgroundColor};
            }
            .ql-container {
                border: ${containerStyle.borderWidth} ${containerStyle.borderLineStyle} ${containerStyle.borderColor} !important;
                color: ${containerStyle.contentColor};
                background-color: ${containerStyle.backgroundColor};
            }
        """.trimIndent()
    }

data class QuillToolbarStyle(
    val borderLineStyle: LineStyle = LineStyle.Solid,
    val borderWidth: CSSLengthOrPercentageNumericValue = 2.px,
    val borderColor: CSSColorValue = rgb(0xCC, 0xCC, 0xCC),
    val backgroundColor: CSSColorValue = Colors.White,
)

data class QuillContainerStyle(
    val borderLineStyle: LineStyle = LineStyle.Solid,
    val borderWidth: CSSLengthOrPercentageNumericValue = 0.px,
    val borderColor: CSSColorValue = Colors.White,
    val backgroundColor: CSSColorValue = Colors.White,
    val contentColor: CSSColorValue = Colors.Black,
)
