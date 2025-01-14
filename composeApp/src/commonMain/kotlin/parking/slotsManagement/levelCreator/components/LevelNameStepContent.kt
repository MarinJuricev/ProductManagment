package parking.slotsManagement.levelCreator.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.TextFieldForm
import org.product.inventory.shared.MR
import parking.slotsManagement.levelCreator.model.LevelNameStep

@Composable
fun LevelNameStepContent(
    uiState: LevelNameStep,
    onLevelNameChanged: (String) -> Unit,
) {
    TextFieldForm(
        modifier = Modifier.padding(vertical = 32.dp),
        formTitle = uiState.levelNameFormTitle,
        value = uiState.levelName,
        onValueChanged = onLevelNameChanged,
        placeholder = {
            BodySmallText(
                text = uiState.levelNamePlaceholder,
                color = MR.colors.textLight,
            )
        },
        errorMessage = uiState.levelNameError,
    )
}
