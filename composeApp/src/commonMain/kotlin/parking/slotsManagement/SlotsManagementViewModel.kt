package parking.slotsManagement

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.reservation.DeleteGarageLevel
import parking.reservation.ObserveGarageLevelsData
import parking.reservation.model.GarageLevelData
import parking.slotsManagement.interaction.SlotsManagementEvent
import parking.slotsManagement.interaction.SlotsManagementEvent.DeleteLevelCanceled
import parking.slotsManagement.interaction.SlotsManagementEvent.DeleteLevelClick
import parking.slotsManagement.interaction.SlotsManagementEvent.DeleteLevelConfirmed
import parking.slotsManagement.interaction.SlotsManagementEvent.EditLevelClick
import parking.slotsManagement.interaction.SlotsManagementEvent.FabClick
import parking.slotsManagement.interaction.SlotsManagementEvent.LevelCreatorDismissRequest
import parking.slotsManagement.interaction.SlotsManagementScreenState.Loading
import parking.slotsManagement.interaction.SlotsManagementViewEffect
import parking.slotsManagement.mapper.SlotsManagementUiMapper

class SlotsManagementViewModel(
    observeGarageLevelData: ObserveGarageLevelsData,
    private val uiMapper: SlotsManagementUiMapper,
    private val deleteGarageLevel: DeleteGarageLevel,
) : ScreenModel {

    private val _viewEffect = Channel<SlotsManagementViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<SlotsManagementViewEffect> = _viewEffect.receiveAsFlow()
    private val levelCreatorVisible = MutableStateFlow(false)
    private val questionDialogVisible = MutableStateFlow(false)
    private val selectedGarageLevel = MutableStateFlow<GarageLevelData?>(null)
    private var selectedGarageId: String = ""

    val uiState = combine(
        observeGarageLevelData(),
        levelCreatorVisible,
        selectedGarageLevel,
        questionDialogVisible,
        uiMapper::map,
    )
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun onEvent(event: SlotsManagementEvent) {
        when (event) {
            is FabClick -> {
                levelCreatorVisible.update { true }
                selectedGarageLevel.update { null }
            }

            is LevelCreatorDismissRequest -> levelCreatorVisible.update { false }
            is DeleteLevelClick -> {
                selectedGarageId = event.garageLevel.id
                questionDialogVisible.update { true }
                selectedGarageLevel.update { event.garageLevel }
            }

            is EditLevelClick -> {
                levelCreatorVisible.update { true }
                selectedGarageLevel.update { event.garageLevel }
            }

            is DeleteLevelCanceled -> {
                questionDialogVisible.update { false }
                selectedGarageLevel.update { null }
            }
            is DeleteLevelConfirmed -> screenModelScope.launch {
                deleteGarageLevel(selectedGarageId)
                questionDialogVisible.update { false }
                selectedGarageLevel.update { null }
            }
        }
    }
}
