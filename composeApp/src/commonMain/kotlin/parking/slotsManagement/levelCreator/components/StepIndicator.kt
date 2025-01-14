package parking.slotsManagement.levelCreator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import components.BodySmallText
import org.product.inventory.shared.MR

@Composable
fun StepIndicator(
    stepIdentifier: String,
    currentStep: String,
    lastStep: String,
    stepSeparator: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        BodySmallText(
            text = stepIdentifier,
        )
        BodySmallText(
            text = currentStep,
            color = MR.colors.secondary,
            fontWeight = SemiBold,
        )
        BodySmallText(
            text = stepSeparator,
            color = MR.colors.secondary,
            fontWeight = SemiBold,
        )
        BodySmallText(
            text = lastStep,
            color = MR.colors.secondary,
            fontWeight = SemiBold,
        )
    }
}
