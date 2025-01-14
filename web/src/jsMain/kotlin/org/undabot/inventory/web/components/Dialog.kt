package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.KeyboardEventInit

enum class ModalSize(val value: String) {
    None(value = ""),
    Small(value = "modal-sm"),
    Large(value = "modal-lg"),
    ExtraLarge(value = "modal-xl"),
}

@Composable
fun Dialog(
    id: String,
    modifier: Modifier = Modifier,
    closableOutside: Boolean = false,
    centered: Boolean = true,
    size: ModalSize = ModalSize.None,
    clearContentOnClose: Boolean = false, // set this flag to true if dialog content contains editable TextArea component
    content: @Composable (Modifier) -> Unit,
    onClose: (() -> Unit)? = null,
) {
    val modalContentId = remember(id) { "$id-modal-content" }

    DisposableEffect(id, onClose) {
        val modalElement = document.getElementById(id)

        val closeAction: (Event) -> Unit = {
            onClose?.invoke()

            // this is workaround for clearing the content of the modal
            // issue related to having TextArea component inside modal and content survives between modal openings
            if (clearContentOnClose) document.getElementById(modalContentId)?.innerHTML = ""
        }

        modalElement?.addEventListener(
            type = EVENT_MODAL_HIDDEN,
            callback = closeAction,
        )

        onDispose {
            modalElement?.removeEventListener(type = EVENT_MODAL_HIDDEN, callback = closeAction)
        }
    }

    Div(
        attrs = modifier
            .id(id)
            .classNames("modal", "fade")
            .thenIf(
                condition = !closableOutside,
                other = Modifier.attrsModifier {
                    attr("data-bs-backdrop", "static")
                },
            )
            .toAttrs {
                attr("tabindex", "-1")
            },
    ) {
        Div(
            attrs = Modifier
                .classNames("modal-dialog")
                .thenIf(
                    condition = size != ModalSize.None,
                    other = Modifier.classNames(size.value),
                )
                .thenIf(
                    condition = centered,
                    other = Modifier.classNames("modal-dialog-centered"),
                )
                .toAttrs(),
        ) {
            Div(
                attrs = Modifier
                    .classNames("modal-content")
                    .toAttrs(),
            ) {
                content(Modifier.id(modalContentId))
            }
        }
    }
}

fun Modifier.showModalOnClick(id: String): Modifier = attrsModifier {
    attr("data-bs-toggle", "modal")
    attr("data-bs-target", "#$id")
}

fun Modifier.hideModalOnClick(): Modifier = attrsModifier {
    attr("data-bs-dismiss", "modal")
}

// This function is used to close the dialog by simulating the Escape key press
fun closeDialog(dialogId: String) {
    document.getElementById(dialogId)?.dispatchEvent(
        event = KeyboardEvent(
            type = "keydown",
            eventInitDict = KeyboardEventInit(
                key = "Escape",
                code = "Escape",
            ),
        ),
    )
}

private const val EVENT_MODAL_HIDDEN = "hidden.bs.modal"
