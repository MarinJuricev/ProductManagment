package parking.slotsManagement.levelCreator

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import components.Image
import components.ImageType.Resource
import components.LoadingIndicator
import org.product.inventory.shared.MR
import parking.reservation.model.GarageLevelData
import parking.slotsManagement.levelCreator.components.LevelCreatorScreenContent
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent
import parking.slotsManagement.levelCreator.interaction.LevelCreatorUiState.Content
import parking.slotsManagement.levelCreator.interaction.LevelCreatorUiState.Loading
import parking.slotsManagement.levelCreator.interaction.LevelCreatorViewEffect.DismissRequested

class LevelCreatorScreen(
    val onDismissRequest: () -> Unit,
    val viewModel: LevelCreatorViewModel,
    val existingGarageLevels: List<GarageLevelData>,
    val selectedGarageLevel: GarageLevelData? = null,
) : Screen {
    @Composable
    override fun Content() {
        val uiState = viewModel.uiState.collectAsState().value

        LaunchedEffect(Unit) {
            viewModel.onEvent(LevelCreatorEvent.LevelCreatorShown(selectedGarageLevel, existingGarageLevels))
            viewModel.viewEffect.collect { viewEffect ->
                when (viewEffect) {
                    DismissRequested -> onDismissRequest()
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(23.dp))
                .background(colorResource(MR.colors.surface.resourceId))
                .animateContentSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onDismissRequest)
                        .padding(16.dp),
                    imageType = Resource(MR.images.close_round),
                )
                when (uiState) {
                    is Content -> LevelCreatorScreenContent(
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                    )

                    is Loading -> LoadingIndicator(modifier = Modifier.fillMaxHeight(0.2f))
                }
            }
        }
    }
}
