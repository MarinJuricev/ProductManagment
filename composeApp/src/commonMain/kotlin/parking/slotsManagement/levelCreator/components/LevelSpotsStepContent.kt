package parking.slotsManagement.levelCreator.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType.Resource
import components.InventoryTextField
import org.product.inventory.shared.MR
import parking.slotsManagement.components.SpotsGrid
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent.AddClick
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent.SpotRemoved
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent.TextInputChanged
import parking.slotsManagement.levelCreator.model.LevelSpotsStep
import utils.clickable

@Composable
fun LevelSpotsStepContent(
    uiState: LevelSpotsStep,
    onEvent: (LevelSpotsStepEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val addButtonColorAlpha by animateFloatAsState(
            if (uiState.addButtonEnabled) 1f else 0.5f,
            label = uiState.inputTextFieldValue,
        )

        AnimatedVisibility(uiState.spots.isEmpty()) {
            BodySmallText(
                modifier = Modifier.fillMaxWidth(0.6f).padding(vertical = 60.dp),
                text = uiState.spotsPlaceholder,
                color = MR.colors.textLight,
                fontWeight = SemiBold,
                textAlign = Center,
            )
        }
        AnimatedVisibility(uiState.spots.isNotEmpty()) {
            SpotsGrid(
                modifier = Modifier
                    .heightIn(min = 90.dp, max = 300.dp)
                    .padding(horizontal = 8.dp, vertical = 32.dp),
                spots = uiState.spots,
                spotIdentifier = uiState.spotLeadingIdentifier,
                removable = true,
                onRemoveClick = { onEvent(SpotRemoved(it)) },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            InventoryTextField(
                modifier = Modifier.fillMaxWidth(0.9f).padding(end = 8.dp),
                value = uiState.inputTextFieldValue,
                onValueChanged = { onEvent(TextInputChanged(it)) },
                shape = RoundedCornerShape(12.dp),
                placeholder = {
                    BodySmallText(
                        text = uiState.inputTextFieldPlaceholder,
                        color = MR.colors.textLight,
                    )
                },
                errorMessage = uiState.inputTextFieldError,
            )

            Box(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(colorResource(MR.colors.secondary.resourceId).copy(alpha = addButtonColorAlpha))
                    .clickable(
                        rippleEffectVisible = uiState.addButtonEnabled,
                    ) {
                        if (uiState.addButtonEnabled) {
                            onEvent(AddClick(uiState.inputTextFieldValue))
                        }
                    },
            ) {
                Image(
                    modifier = Modifier.align(Alignment.Center).padding(8.dp),
                    imageType = Resource(MR.images.plus),
                )
            }
        }
    }
}
