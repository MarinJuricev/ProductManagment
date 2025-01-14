package org.product.inventory.web.pages.emailtemplates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.animation.Keyframes
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.components.LoadingButton
import org.product.inventory.web.components.QuillEditor
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.invisibleModifier
import org.product.inventory.web.components.maxLines
import org.product.inventory.web.components.provideCustomColorScheme
import org.product.inventory.web.components.rememberBreakpoint
import org.product.inventory.web.components.toPersistentAnimation
import org.product.inventory.web.toCssColor

val emailTemplatesWithSelectedTemplateStyle by ComponentStyle {
    Breakpoint.ZERO {
        invisibleModifier
    }

    Breakpoint.MD {
        Modifier.display(DisplayStyle.Flex).width(50.percent)
    }
}

val emailTemplatesWithoutSelectedTemplateStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.margin(right = 20.px).width(100.percent)
    }

    Breakpoint.MD {
        Modifier.margin(0.px).width(100.percent)
    }
}

val quillEditorWithSelectedTemplateStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.margin(right = 20.px, bottom = 20.px).width(100.percent)
    }

    Breakpoint.MD {
        Modifier.margin(0.px).width(50.percent)
    }
}

val quillEditorWithoutSelectedTemplateStyle by ComponentStyle {
    Breakpoint.ZERO {
        invisibleModifier
    }
}

val emailTemplatesItemIconStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.size(60.px)
    }

    Breakpoint.LG {
        Modifier.size(80.px)
    }
}

val closeButtonQuillEditorStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.left(30.px)
    }

    Breakpoint.SM {
        Modifier.left(40.px)
    }

    Breakpoint.MD {
        invisibleModifier
    }
}

val saveButtonQuillEditorStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.margin(bottom = 10.px, right = 30.px)
    }

    Breakpoint.MD {
        Modifier.margin(10.px)
    }
}

val TemplatesListHalfSizeKeyFrames by Keyframes {
    from { Modifier.width(100.percent) }
    to { Modifier.width(50.percent) }
}

val TemplatesListFullSizeKeyFrames by Keyframes {
    from { Modifier.width(50.percent) }
    to { Modifier.width(100.percent) }
}

val EditorHalfSizeSizeKeyFrames by Keyframes {
    from { Modifier.width(0.percent) }
    to { Modifier.width(50.percent) }
}

val EditorZeroSizeKeyFrames by Keyframes {
    from { Modifier.width(50.percent) }
    to { Modifier.width(0.percent) }
}

val SaveButtonVisibleKeyFrames by Keyframes {
    from { Modifier.opacity(0) }
    to { Modifier.opacity(1) }
}

val SaveButtonInvisibleKeyFrames by Keyframes {
    from { Modifier.opacity(1) }
    to { Modifier.opacity(0) }
}

@Composable
fun EmailTemplates(
    state: EmailTemplatesUiState,
    onEvent: (EmailTemplatesEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TitleWithPath(
            items = state.breadcrumbItems,
            title = state.title,
            onPathClick = { onEvent(EmailTemplatesEvent.PathClick(it)) },
        )

        when {
            state.isLoading -> LoadingContent()
            else -> when (state.infoMessage != null) {
                true -> InfoContent(message = state.infoMessage)
                else -> SuccessContent(state = state, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun SuccessContent(
    state: EmailTemplatesUiState,
    modifier: Modifier = Modifier,
    onEvent: (EmailTemplatesEvent) -> Unit,
) {
    val breakpoint by rememberBreakpoint()

    Row(modifier = modifier.fillMaxSize()) {
        TemplatesList(
            modifier = Modifier
                .fillMaxHeight()
                .thenIf(
                    condition = state.selectedTemplate != null,
                    other = emailTemplatesWithSelectedTemplateStyle.toModifier(),
                )
                .thenIf(
                    condition = state.selectedTemplate == null,
                    other = emailTemplatesWithoutSelectedTemplateStyle.toModifier(),
                )
                .thenIf(
                    condition = breakpoint >= Breakpoint.MD && state.showAnimation,
                    other = Modifier.animation(
                        when (state.selectedTemplate == null) {
                            true -> TemplatesListFullSizeKeyFrames.toPersistentAnimation()
                            false -> TemplatesListHalfSizeKeyFrames.toPersistentAnimation()
                        },
                    ),
                ),
            state = state,
            onEvent = onEvent,
        )

        Box(
            modifier = Modifier
                .thenIf(
                    condition = state.selectedTemplate != null,
                    other = quillEditorWithSelectedTemplateStyle.toModifier(),
                )
                .thenIf(
                    condition = state.selectedTemplate == null,
                    other = quillEditorWithoutSelectedTemplateStyle.toModifier(),
                )
                .thenIf(
                    condition = breakpoint >= Breakpoint.MD && state.showAnimation,
                    other = Modifier.animation(
                        when (state.selectedTemplate == null) {
                            true -> EditorZeroSizeKeyFrames.toPersistentAnimation()
                            false -> EditorHalfSizeSizeKeyFrames.toPersistentAnimation()
                        },
                    ),
                )
                .fillMaxHeight(),
        ) {
            state.selectedTemplate?.let { template ->
                QuillEditor(
                    id = template.id,
                    content = template.editContent,
                    modifier = Modifier.fillMaxSize(),
                    enabled = !state.saveInProgress,
                    onTextChanged = { content ->
                        onEvent(EmailTemplatesEvent.TemplateChanged(template.id, content))
                    },
                )

                Button(
                    modifier = closeButtonQuillEditorStyle
                        .toModifier()
                        .align(Alignment.BottomEnd)
                        .position(Position.Fixed)
                        .bottom(0.px)
                        .margin(10.px)
                        .padding(leftRight = 60.px),
                    colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
                    enabled = !state.saveInProgress,
                    content = {
                        Text(
                            modifier = Modifier.color(Colors.White),
                            value = state.closeButtonText,
                        )
                    },
                    onClick = { onEvent(EmailTemplatesEvent.CloseButtonClick) },
                )
            }

            LoadingButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .position(Position.Fixed)
                    .right(0.px)
                    .bottom(0.px)
                    .padding(leftRight = 60.px)
                    .then(saveButtonQuillEditorStyle.toModifier())
                    .thenIf(
                        condition = breakpoint >= Breakpoint.MD && state.showAnimation,
                        other = Modifier.animation(
                            when (state.selectedTemplate == null) {
                                true -> SaveButtonInvisibleKeyFrames.toPersistentAnimation()
                                false -> SaveButtonVisibleKeyFrames.toPersistentAnimation()
                            },
                        ),
                    ),
                isLoading = state.saveInProgress,
                content = {
                    Text(
                        modifier = Modifier.color(Colors.White),
                        value = state.saveButtonText,
                    )
                },
                onClick = { onEvent(EmailTemplatesEvent.UpdateTemplate) },
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressBar()
    }
}

@Composable
private fun InfoContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            value = message,
            modifier = Modifier
                .fontWeight(FontWeight.SemiBold)
                .padding(30.px),
            size = 16.px,
        )
    }
}

@Composable
private fun TemplatesList(
    state: EmailTemplatesUiState,
    modifier: Modifier = Modifier,
    onEvent: (EmailTemplatesEvent) -> Unit,
) {
    Column(modifier = modifier) {
        state.items.forEach { item ->
            TemplateItem(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.px)
                    .background(color = item.backgroundColor)
                    .thenIf(
                        condition = item.isSelected,
                        other = Modifier.border {
                            color(MR.colors.textLight.toCssColor())
                            style(LineStyle.Solid)
                            width(1.px)
                        },
                    )
                    .thenIf(
                        condition = !state.saveInProgress,
                        other = Modifier.clickable { onEvent(EmailTemplatesEvent.TemplateClick(item.id)) },
                    ),
            )
        }
    }
}

@Composable
private fun TemplateItem(
    item: TemplateItemUi,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Div(
            attrs = Modifier
                .fillMaxWidth()
                .display(DisplayStyle.Flex)
                .toAttrs(),
        ) {
            Box(
                modifier = emailTemplatesItemIconStyle
                    .toModifier()
                    .background(color = MR.colors.secondary.toCssColor())
                    .clip(Circle(45))
                    .margin(right = 20.px),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    src = item.icon,
                    modifier = Modifier.size(35.px),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().margin(top = 5.px),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    modifier = Modifier
                        .color(MR.colors.secondary.toCssColor())
                        .fontWeight(FontWeight.SemiBold),
                    size = 16.px,
                    value = item.title,
                )
                Text(
                    modifier = Modifier
                        .color(MR.colors.textBlack.toCssColor())
                        .fontWeight(400)
                        .maxLines(2),
                    size = 14.px,
                    value = item.text,
                )
            }
        }
    }
}
