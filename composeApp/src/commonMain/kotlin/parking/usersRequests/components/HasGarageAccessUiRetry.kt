package parking.usersRequests.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import components.BodyMediumText
import components.BodySmallText
import components.InventoryBigButton
import org.product.inventory.shared.MR
import parking.usersRequests.details.model.HasGarageAccessUi.Retry

@Composable
fun HasGarageAccessUiRetry(
    uiState: Retry,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BodyMediumText(
            text = uiState.errorMessage,
        )
        InventoryBigButton(onClick = onRetry) {
            BodySmallText(
                text = uiState.buttonTitle,
                color = MR.colors.surface,
                fontWeight = SemiBold,
            )
        }
    }
}
