package parking.slotsManagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import components.Image
import components.ImageType.Resource
import components.LoadingIndicator
import components.QuestionDialog
import components.TitleLargeText
import org.koin.compose.koinInject
import org.product.inventory.shared.MR
import parking.slotsManagement.components.GarageLevelUi
import parking.slotsManagement.interaction.SlotsManagementEvent
import parking.slotsManagement.interaction.SlotsManagementEvent.DeleteLevelCanceled
import parking.slotsManagement.interaction.SlotsManagementEvent.DeleteLevelConfirmed
import parking.slotsManagement.interaction.SlotsManagementEvent.FabClick
import parking.slotsManagement.interaction.SlotsManagementEvent.LevelCreatorDismissRequest
import parking.slotsManagement.interaction.SlotsManagementScreenState.Content
import parking.slotsManagement.interaction.SlotsManagementScreenState.Loading
import parking.slotsManagement.levelCreator.LevelCreatorScreen

class SlotsManagementScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: SlotsManagementViewModel = koinScreenModel()
        when (val uiState = viewModel.uiState.collectAsState().value) {
            is Content -> SlotsManagementContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )

            is Loading -> LoadingIndicator()
        }
    }
}

@Composable
private fun SlotsManagementContent(
    uiState: Content,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(23.dp),
        ) {
            item {
                TitleLargeText(uiState.screenTitle, fontWeight = SemiBold)
            }
            items(
                items = uiState.levels,
                key = { level -> level.id },
            ) {
                GarageLevelUi(
                    uiContent = uiState,
                    garageLevelData = it,
                    onEvent = onEvent,
                )
            }
        }

        if (uiState.dialogData.isVisible) {
            QuestionDialog(
                questionDialogData = uiState.dialogData,
                onPositiveActionClick = { onEvent(DeleteLevelConfirmed) },
                onDismissRequest = { onEvent(DeleteLevelCanceled) },
                onNegativeActionClick = { onEvent(DeleteLevelCanceled) },
            )
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            backgroundColor = colorResource(MR.colors.secondary.resourceId),
            onClick = { onEvent(FabClick) },
        ) {
            Image(
                modifier = Modifier.padding(8.dp).size(16.dp),
                imageType = Resource(MR.images.plus),
            )
        }

        if (uiState.levelCreatorVisible) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Navigator(
                    onBackPressed = { false },
                    screen = LevelCreatorScreen(
                        onDismissRequest = { onEvent(LevelCreatorDismissRequest) },
                        viewModel = koinInject(),
                        selectedGarageLevel = uiState.selectedGarageLevel,
                        existingGarageLevels = uiState.levels,
                    ),
                )
            }
        }
    }
}
