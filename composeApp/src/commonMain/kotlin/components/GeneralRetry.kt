package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR

@Composable
fun GeneralRetry(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodyLargeText(
            stringResource(MR.strings.general_retry_error_title.resourceId),
            fontWeight = SemiBold,
        )
        BodyLargeText(
            text = stringResource(MR.strings.general_unknown_error_message.resourceId),
            textAlign = Center,
        )
        InventoryBigButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRetryClick,
        ) {
            BodyMediumText(
                stringResource(MR.strings.general_retry_button_text.resourceId),
                color = MR.colors.surface,
                fontWeight = SemiBold,
            )
        }
    }
}
