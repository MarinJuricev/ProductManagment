package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.modifiers.onMouseOver
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.thenIf
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

data class ConfirmationDialogState(
    val title: String,
    val positiveText: String,
    val negativeText: String,
    val message: String? = null,
)

@Composable
fun ConfirmationDialog(
    id: String,
    dialogState: ConfirmationDialogState?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    closableOutside: Boolean = true,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onClose: (() -> Unit)? = null,
) {
    Dialog(
        id = id,
        modifier = modifier,
        closableOutside = closableOutside,
        onClose = onClose,
        content = { contentModifier ->
            dialogState?.let { state ->
                Column(
                    modifier = contentModifier.padding(10.px),
                ) {
                    Text(
                        value = state.title,
                        size = 22.px,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .margin(bottom = 20.px)
                            .fontWeight(FontWeight.Bold)
                            .color(MR.colors.textBlack.toCssColor()),
                    )

                    state.message?.let {
                        Text(
                            value = it,
                            size = 18.px,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .margin(bottom = 20.px)
                                .color(MR.colors.textBlack.toCssColor()),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().spaceBetween(),
                    ) {
                        ConfirmationDialogButton(
                            text = state.negativeText,
                            isLoading = isLoading,
                            onClick = onNegativeClick,
                        )

                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressBar()
                            }
                        }

                        ConfirmationDialogButton(
                            text = state.positiveText,
                            isLoading = isLoading,
                            onClick = onPositiveClick,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun ConfirmationDialogButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var buttonBackgroundColor by remember { mutableStateOf(Colors.Transparent) }
    var buttonTextColor by remember { mutableStateOf(MR.colors.textBlack.toCssColor()) }

    Box(
        modifier = modifier
            .padding(6.px)
            .roundedCornersShape(8.px)
            .background(color = buttonBackgroundColor)
            .thenIf(
                condition = !isLoading,
                other = Modifier.onMouseOver {
                    buttonBackgroundColor = Colors.Grey
                    buttonTextColor = Colors.White
                }.clickable(onClick),
            ).onMouseLeave {
                buttonBackgroundColor = Colors.Transparent
                buttonTextColor = MR.colors.textBlack.toCssColor()
            },
    ) {
        Text(
            size = 22.px,
            value = text,
            modifier = Modifier.fontWeight(FontWeight.Bold).color(buttonTextColor),
        )
    }
}
