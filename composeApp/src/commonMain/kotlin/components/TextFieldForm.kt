package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR

@Composable
fun TextFieldForm(
    formTitle: String,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodySmallText(
            text = formTitle,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        InventoryTextField(
            value = value,
            onValueChanged = onValueChanged,
            textStyle = BodySmallTextStyle,
            placeholder = placeholder,
            shape = RoundedCornerShape(12.dp),
            errorMessage = errorMessage,
            enabled = enabled,
        )
    }
}
