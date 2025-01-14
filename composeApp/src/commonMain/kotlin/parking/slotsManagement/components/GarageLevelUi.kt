package parking.slotsManagement.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.InventoryDropDownPicker
import org.product.inventory.shared.MR
import parking.reservation.model.GarageLevelData
import parking.slotsManagement.interaction.GarageLevelOption
import parking.slotsManagement.interaction.GarageLevelOption.Delete
import parking.slotsManagement.interaction.GarageLevelOption.Edit
import parking.slotsManagement.interaction.SlotsManagementEvent
import parking.slotsManagement.interaction.SlotsManagementEvent.DeleteLevelClick
import parking.slotsManagement.interaction.SlotsManagementEvent.EditLevelClick
import parking.slotsManagement.interaction.SlotsManagementScreenState
import utils.clickable

@Composable
fun GarageLevelUi(
    garageLevelData: GarageLevelData,
    uiContent: SlotsManagementScreenState.Content,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    var isVisible by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .background(colorResource(MR.colors.surface.resourceId))
            .clickable(rippleEffectVisible = false) { isVisible = !isVisible },
    ) {
        LevelInfo(
            levelIdentifier = uiContent.itemLevelIdentifier,
            level = garageLevelData.level.title,
            isExpanded = isVisible,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            GarageLevelHeaderInfo(
                uiContent,
                garageLevelData,
                onOptionsClick = {
                    when (it) {
                        is Delete -> onEvent(DeleteLevelClick(garageLevelData))
                        is Edit -> onEvent(EditLevelClick(garageLevelData))
                    }
                },
            )
            AnimatedVisibility(isVisible) {
                SpotsGrid(
                    modifier = Modifier.heightIn(min = 90.dp, max = 400.dp)
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    spots = garageLevelData.spots,
                    spotIdentifier = uiContent.leadingSpotIdentifierText,
                )
            }
        }
    }
}

@Composable
private fun GarageLevelHeaderInfo(
    uiContent: SlotsManagementScreenState.Content,
    garageLevelData: GarageLevelData,
    onOptionsClick: (GarageLevelOption) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            BodySmallText(
                text = uiContent.numberOfSpotsIdentifier,
                modifier = Modifier.padding(vertical = 16.dp),
                color = MR.colors.textBlack,
                fontWeight = FontWeight.SemiBold,
            )
            BodySmallText(
                text = garageLevelData.spots.size.toString(),
                modifier = Modifier.padding(vertical = 16.dp),
                color = MR.colors.secondary,
                fontWeight = FontWeight.Bold,
            )
        }

        InventoryDropDownPicker(
            menuItems = uiContent.availableOptions,
            selectedItem = null,
            placeholder = { Image(uiContent.itemMenuIconResource) },
            borderColor = MR.colors.surface,
            onItemClick = { it?.let { option -> onOptionsClick(option) } },

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),

            ) {
                it?.let { icon -> Image(modifier = Modifier.size(16.dp), imageType = icon.iconResource) }
                if (it?.displayedText?.isNotEmpty() == true) {
                    BodySmallText(it.displayedText)
                }
            }
        }
    }
}
