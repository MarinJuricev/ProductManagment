package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.zIndex
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.CheckIcon
import com.varabyte.kobweb.silk.components.icons.CloseIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

data class AlertMessage(
    val message: String = "",
    val isSuccess: Boolean = true,
    val backgroundColor: Color.Rgb = MR.colors.alertMessageSuccessBackground.toCssColor(),
    val contentColor: Color.Rgb = MR.colors.alertMessageErrorBackground.toCssColor(),
    val alertMessageDuration: Long = 3000L,
)

suspend fun MutableStateFlow<AlertMessage?>.clearAfterDelay() {
    delay(value?.alertMessageDuration ?: 0L)
    update { null }
}

@Composable
fun AlertMessage(
    alertMessage: AlertMessage,
    modifier: Modifier = Modifier,
    cornerShapeSize: CSSSizeValue<CSSUnit.px> = 5.px,
    alertMessageContentSize: CSSSizeValue<CSSUnit.px> = 18.px,
) {
    Div(
        attrs = modifier
            .styleModifier {
                position(Position.Fixed)
                bottom(0.px)
                zIndex(999)
            }
            .toAttrs(),
    ) {
        Div(
            attrs = Modifier
                .padding(all = 16.px)
                .styleModifier {
                    property("pointer-events", "none")
                }
                .toAttrs(),
        ) {
            Div(
                attrs = Modifier
                    .styleModifier {
                        property("pointer-events", "auto")
                    }
                    .toAttrs {
                        attr("role", "alert")
                        attr("aria-live", "assertive")
                        attr("aria-atomic", "true")
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 16.px)
                        .background(alertMessage.backgroundColor)
                        .borderRadius(cornerShapeSize)
                        .border(
                            color = MR.colors.textLight.toCssColor(),
                            style = LineStyle.Solid,
                            width = 1.px,
                        )
                        .roundedCornersShape(cornerShapeSize),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val iconModifier = Modifier
                        .size(alertMessageContentSize)
                        .color(alertMessage.contentColor)

                    if (alertMessage.isSuccess) {
                        CheckIcon(iconModifier)
                    } else {
                        CloseIcon(iconModifier)
                    }

                    Box(modifier = Modifier.width(16.px))

                    Text(
                        value = alertMessage.message,
                        modifier = Modifier.color(alertMessage.contentColor),
                        size = alertMessageContentSize,
                    )
                }
            }
        }
    }
}
