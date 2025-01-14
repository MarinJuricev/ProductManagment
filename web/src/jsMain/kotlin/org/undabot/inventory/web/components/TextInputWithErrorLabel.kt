package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.silk.components.forms.TextInput
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

@Composable
fun TextInputWithErrorMessage(
    value: String,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    errorTextColor: CSSColorValue = MR.colors.error.toCssColor(),
    errorTextSize: CSSLengthOrPercentageNumericValue? = 12.px,
    errorTextWeight: FontWeight = FontWeight.SemiBold,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        TextInput(
            modifier = Modifier.fillMaxWidth(),
            text = value,
            enabled = enabled,
            placeholder = placeholder,
            onTextChanged = {
                onValueChange(it)
            },
        )

        errorText?.let { error ->
            ErrorText(
                value = error,
                errorTextColor = errorTextColor,
                errorTextSize = errorTextSize,
                errorTextWeight = errorTextWeight,
            )
        }
    }
}

@Composable
fun ErrorText(
    value: String,
    modifier: Modifier = Modifier,
    errorTextColor: CSSColorValue = MR.colors.error.toCssColor(),
    errorTextSize: CSSLengthOrPercentageNumericValue? = 12.px,
    errorTextWeight: FontWeight = FontWeight.SemiBold,
) {
    TextWithOverflow(
        value = value,
        modifier = modifier
            .color(errorTextColor)
            .fontWeight(errorTextWeight),
        size = errorTextSize,
    )
}
