package parking.reservation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.DatePickerField
import org.product.inventory.shared.MR

@Composable
internal fun FormDatePicker(
    title: String,
    confirmButtonText: String,
    displayedDate: String,
    selectedTimestamp: Long,
    onDateSelected: (timestamp: Long) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true,
    lowerDateLimit: Long = 0,
    upperDateLimit: Long = Long.MAX_VALUE,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodySmallText(text = title, fontWeight = FontWeight.SemiBold, color = MR.colors.secondary)
        DatePickerField(
            displayedDate = displayedDate,
            selectedTimestamp = selectedTimestamp,
            onDateSelected = onDateSelected,
            confirmButtonText = confirmButtonText,
            errorMessage = errorMessage,
            enabled = enabled,
            lowerDateLimit = lowerDateLimit,
            upperDateLimit = upperDateLimit,
        )
    }
}
