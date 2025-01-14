package parking.usersRequests.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.InventoryDropDownPicker
import components.LoadingIndicator
import org.product.inventory.shared.MR
import parking.usersRequests.details.model.GarageLevelUi

@Composable
fun FormGarageLevelPicker(
    levelPickerTitle: String,
    availableGarageLevels: List<GarageLevelUi>,
    onLevelSelected: (GarageLevelUi) -> Unit,
    currentGarageLevel: GarageLevelUi,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodySmallText(
            text = levelPickerTitle,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        InventoryDropDownPicker(
            menuItems = availableGarageLevels,
            selectedItem = currentGarageLevel,
            trailingIcon = MR.images.drop_down_icon,
            onItemClick = { it?.let { level -> onLevelSelected(level) } },
            enabled = enabled,
        ) {
            when (it) {
                is GarageLevelUi.GarageLevelUiModel -> BodySmallText(
                    modifier = Modifier.widthIn(min = 40.dp),
                    text = it.title,
                )

                is GarageLevelUi.Undefined -> BodySmallText(
                    modifier = Modifier.widthIn(min = 40.dp),
                    text = stringResource(MR.strings.general_minus.resourceId),
                )

                is GarageLevelUi.Loading -> LoadingIndicator(modifier = Modifier.size(12.dp))
                is GarageLevelUi.Deselected -> BodySmallText(
                    modifier = Modifier.widthIn(min = 40.dp),
                    text = stringResource(MR.strings.general_minus.resourceId),
                )

                null -> {}
            }
        }
    }
}
