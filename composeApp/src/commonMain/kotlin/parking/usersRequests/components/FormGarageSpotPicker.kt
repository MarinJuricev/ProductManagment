package parking.usersRequests.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.InventoryDropDownPicker
import org.product.inventory.shared.MR
import parking.usersRequests.details.model.GarageSpotUi

@Composable
fun FormGarageSpotPicker(
    spotPickerTitle: String,
    availableGarageSpots: List<GarageSpotUi>,
    onSpotSelected: (GarageSpotUi) -> Unit,
    currentGarageSpot: GarageSpotUi,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BodySmallText(
            text = spotPickerTitle,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        InventoryDropDownPicker(
            menuItems = availableGarageSpots,
            selectedItem = currentGarageSpot,
            trailingIcon = MR.images.drop_down_icon,
            onItemClick = { it?.let { spot -> onSpotSelected(spot) } },
            enabled = enabled,
        ) {
            BodySmallText(
                modifier = Modifier.widthIn(min = 40.dp),
                text = when (it) {
                    GarageSpotUi.Deselected -> stringResource(MR.strings.general_minus.resourceId)
                    is GarageSpotUi.GarageSpotUiModel -> it.spot.title
                    GarageSpotUi.Undefined -> stringResource(MR.strings.general_minus.resourceId)
                    null -> ""
                },
            )
        }
    }
}
