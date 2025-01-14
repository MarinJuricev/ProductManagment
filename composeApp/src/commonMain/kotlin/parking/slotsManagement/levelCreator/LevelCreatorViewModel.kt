package parking.slotsManagement.levelCreator

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import core.utils.UUIDProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.CreateGarageLevel
import parking.reservation.UpdateGarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.GarageLevelRequest
import parking.reservation.model.ParkingSpot
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.LevelCreatorShown
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.LevelNameChange
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.NextStepClick
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.PreviousStepClick
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.SaveClick
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.SpotCreated
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.SpotNameChange
import parking.slotsManagement.levelCreator.interaction.LevelCreatorEvent.SpotRemoved
import parking.slotsManagement.levelCreator.interaction.LevelCreatorUiState
import parking.slotsManagement.levelCreator.interaction.LevelCreatorViewEffect
import parking.slotsManagement.levelCreator.interaction.LevelCreatorViewEffect.DismissRequested
import parking.slotsManagement.levelCreator.mapper.LevelCreatorEditableFields
import parking.slotsManagement.levelCreator.mapper.LevelCreatorTextsMapper
import parking.slotsManagement.levelCreator.mapper.LevelCreatorUiMapper

class LevelCreatorViewModel(
    private val uiMapper: LevelCreatorUiMapper,
    private val updateGarageLevel: UpdateGarageLevel,
    private val createGarageLevel: CreateGarageLevel,
    private val uuidProvider: UUIDProvider,
    private val dictionary: Dictionary,
    levelCreatorTextsMapper: LevelCreatorTextsMapper,
) : ScreenModel {

    private val editableFields = MutableStateFlow(LevelCreatorEditableFields())
    private val _spots =
        MutableStateFlow(editableFields.value.selectedGarageLevel?.spots ?: listOf())
    private val _viewEffect = Channel<LevelCreatorViewEffect>(Channel.BUFFERED)
    private val _screenTexts = MutableStateFlow(levelCreatorTextsMapper.map())
    val viewEffect: Flow<LevelCreatorViewEffect> = _viewEffect.receiveAsFlow()

    val uiState = combine(
        editableFields,
        _spots,
        _screenTexts,
        uiMapper::map,
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = LevelCreatorUiState.Loading,
    )

    fun onEvent(event: LevelCreatorEvent) {
        when (event) {
            is LevelCreatorShown -> handleCreatorShown(event)
            is LevelNameChange -> editableFields.update { it.copy(levelName = event.newName) }
            is NextStepClick -> editableFields.update { it.copy(currentStep = it.currentStep + 1) }
            is PreviousStepClick -> editableFields.update { it.copy(currentStep = it.currentStep - 1) }
            is SpotNameChange -> editableFields.update { it.copy(spotName = event.newName) }
            is SpotCreated -> handleSpotCreated(event)
            is SpotRemoved -> handleSpotRemoved(event.spotId)
            is SaveClick -> handleSaveClick(event)
        }
    }

    private fun handleCreatorShown(event: LevelCreatorShown) {
        editableFields.update {
            it.copy(
                selectedGarageLevel = event.selectedGarageLevel,
                existingGarageLevels = event.existingGarageLevels,
            )
        }
        _spots.update { event.selectedGarageLevel?.spots ?: listOf() }
    }

    private fun handleSpotCreated(event: SpotCreated) {
        if (_spots.value.any { it.title == event.spotName }) {
            editableFields.update { it.copy(spotNameError = dictionary.getString(MR.strings.slots_management_duplicated_spot_error)) }
        } else {
            editableFields.update { it.copy(spotName = "", spotNameError = null) }
            _spots.update {
                it + ParkingSpot(
                    id = uuidProvider.generateUUID(),
                    title = event.spotName.trim(),
                )
            }
        }
    }

    private fun handleSpotRemoved(spotId: String) {
        _spots.update {
            it.filterNot { spot -> spotId == spot.id }
        }
    }

    private fun handleSaveClick(
        event: SaveClick,
    ) = screenModelScope.launch {
        editableFields.value.selectedGarageLevel?.let {
            updateGarageLevel(
                garageLevelData = GarageLevelData(
                    id = it.id,
                    level = event.garageLevel,
                    spots = event.spots,
                ),
            )
        } ?: createGarageLevel(
            GarageLevelRequest(
                garageLevel = event.garageLevel,
                spots = event.spots,
            ),
        )

        _viewEffect.send(DismissRequested)
    }
}
