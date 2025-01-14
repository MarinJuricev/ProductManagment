package parking.slotsManagement.levelCreator.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import org.product.inventory.shared.MR
import parking.slotsManagement.levelCreator.model.LevelCreatorStepOptionState
import utils.clickable

@Composable
fun LevelCreatorDialogOption(
    optionName: String,
    optionState: LevelCreatorStepOptionState,
    onOptionClick: () -> Unit,
) {
    BodySmallText(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(
                rippleEffectVisible = optionState is LevelCreatorStepOptionState.Enabled,
            ) {
                if (optionState is LevelCreatorStepOptionState.Enabled) onOptionClick()
            }
            .padding(8.dp),
        text = optionName,
        color = when (optionState) {
            is LevelCreatorStepOptionState.Disabled -> MR.colors.textLight
            is LevelCreatorStepOptionState.Enabled -> MR.colors.secondary
            is LevelCreatorStepOptionState.Invisible -> MR.colors.surface
        },
        fontWeight = FontWeight.SemiBold,
    )
}
