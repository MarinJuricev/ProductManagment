package parking.reservation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.BodySmallTextStyle
import components.InventoryTextField
import org.product.inventory.shared.MR

@Composable
internal fun FormAdditionalNotesForAdmin(
    title: String,
    noteText: String,
    onNoteChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodySmallText(text = title, fontWeight = SemiBold, color = MR.colors.secondary)
        InventoryTextField(
            modifier = Modifier.heightIn(min = 120.dp, max = 120.dp),
            value = noteText,
            onValueChanged = onNoteChanged,
            textStyle = BodySmallTextStyle,
            placeholder = placeholder,
            enabled = enabled,
        )
    }
}
