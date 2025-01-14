package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ColorResource
import org.product.inventory.shared.MR

@Composable
fun InventoryTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = BodySmallTextStyle,
    textColor: ColorResource = MR.colors.textBlack,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = textStyle.lineHeight,
    fontWeight: FontWeight? = textStyle.fontWeight,
    placeholder: @Composable (() -> Unit)? = null,
    focusedColor: ColorResource = MR.colors.textLight,
    unfocusedColor: ColorResource = MR.colors.textLight,
    cursorColor: ColorResource = MR.colors.secondary,
    errorColor: ColorResource = MR.colors.error,
    handleColor: ColorResource = cursorColor,
    enabled: Boolean = true,
    isError: Boolean = false,
    shape: Shape = RoundedCornerShape(23.dp),
    errorMessage: String? = null,
    textPaddingValues: PaddingValues = PaddingValues(16.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = colorResource(handleColor.resourceId),
        backgroundColor = colorResource(handleColor.resourceId).copy(alpha = 0.3f),
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor by animateColorAsState(
        if (isError) {
            colorResource(errorColor.resourceId)
        } else {
            if (isFocused) colorResource(focusedColor.resourceId) else colorResource(unfocusedColor.resourceId)
        },
        label = value,
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Column {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = shape,
                    ),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChanged,
                    modifier = Modifier.fillMaxWidth().padding(textPaddingValues),
                    interactionSource = interactionSource,
                    enabled = enabled,
                    cursorBrush = SolidColor(colorResource(cursorColor.resourceId)),
                    textStyle = textStyle.copy(
                        color = colorResource(textColor.resourceId),
                        textAlign = textAlign,
                        lineHeight = lineHeight,
                        fontWeight = fontWeight,
                    ),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            placeholder?.let { it() }
                        }
                        innerTextField()
                    },
                    keyboardOptions = keyboardOptions,
                )
            }

            AnimatedVisibility(errorMessage != null) {
                BodySmallText(
                    modifier = Modifier.padding(8.dp),
                    text = errorMessage.orEmpty(),
                    color = MR.colors.error,
                )
            }
        }
    }
}
