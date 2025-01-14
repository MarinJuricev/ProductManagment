package parking.slotsManagement.levelCreator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.TitleLargeText
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.LevelNameChange
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.NextStepClick
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.PreviousStepClick
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.SpotCreated
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.SpotNameChange
import parking.slotsManagement.levelCreator.interaction.LevelCreatorUiState
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent.AddClick
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent.SpotRemoved
import parking.slotsManagement.levelCreator.interaction.LevelSpotsStepEvent.TextInputChanged
import parking.slotsManagement.levelCreator.model.LevelNameStep
import parking.slotsManagement.levelCreator.model.LevelSpotsStep

@Composable
fun LevelCreatorScreenContent(
    uiState: LevelCreatorUiState.Content,
    onEvent: (LevelCreatorEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, bottom = 8.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleLargeText(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            text = uiState.screenTitle,
            textAlign = TextAlign.Center,
            fontWeight = SemiBold,
        )
        StepIndicator(
            stepIdentifier = uiState.stepIdentifier,
            currentStep = uiState.currentStepText,
            stepSeparator = uiState.stepSeparator,
            lastStep = uiState.lastStepText,
        )
        when (val step = uiState.currentStep) {
            is LevelNameStep -> LevelNameStepContent(
                uiState = step,
                onLevelNameChanged = { onEvent(LevelNameChange(it)) },
            )

            is LevelSpotsStep -> LevelSpotsStepContent(
                uiState = step,
                onEvent = {
                    when (it) {
                        is AddClick -> onEvent(SpotCreated(it.spotName))
                        is SpotRemoved -> onEvent(LevelCreatorEvent.SpotRemoved(it.spot.id))
                        is TextInputChanged -> onEvent(SpotNameChange(it.value))
                    }
                },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LevelCreatorDialogOption(
                optionName = uiState.currentStep.firstOptionName,
                optionState = uiState.currentStep.firstOptionState,
                onOptionClick = {
                    onEvent(
                        uiState.currentStep.firstOptionEvent ?: PreviousStepClick,
                    )
                },
            )
            LevelCreatorDialogOption(
                optionName = uiState.currentStep.secondOptionName,
                optionState = uiState.currentStep.secondOptionState,
                onOptionClick = { onEvent(uiState.currentStep.secondOptionEvent ?: NextStepClick) },
            )
        }
    }
}
