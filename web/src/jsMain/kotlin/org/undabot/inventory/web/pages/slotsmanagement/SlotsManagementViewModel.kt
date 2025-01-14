package org.product.inventory.web.pages.slotsmanagement

import core.utils.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.clearAfterDelay
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import parking.dashboard.model.ParkingDashboardOption
import parking.reservation.CheckInputFieldLength
import parking.reservation.CreateGarageLevel
import parking.reservation.DeleteGarageLevel
import parking.reservation.ObserveGarageLevelsData
import parking.reservation.UpdateGarageLevel
import parking.reservation.model.GarageLevel
import parking.reservation.model.GarageLevelData
import parking.reservation.model.GarageLevelRequest
import parking.reservation.model.ParkingSpot
import parking.role.ObserveScreenUnavailability

class SlotsManagementViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: SlotsManagementUiMapper,
    private val deleteGarageLevel: DeleteGarageLevel,
    private val createGarageLevel: CreateGarageLevel,
    private val updateGarageLevel: UpdateGarageLevel,
    private val checkInputFieldLength: CheckInputFieldLength,
    private val alertMessageMapper: AlertMessageMapper,
    private val dictionary: Dictionary,
    observeGarageLevelData: ObserveGarageLevelsData,
    observeScreenUnavailability: ObserveScreenUnavailability,
) {

    private val _uiStateFlags = MutableStateFlow(UiStateFlags())
    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)
    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val _garageLevelForDeletion = MutableStateFlow<GarageLevelDataUi?>(null)
    val state = combine(
        _uiStateFlags,
        _routeToNavigate,
        observeGarageLevelData().onEach { _uiStateFlags.update { it.copy(isLoading = false) } },
        _garageLevelForDeletion.map { it?.title },
        _alertMessage,
        uiMapper::toUiState,
    ).stateIn(
        scope = scope,
        initialValue = SlotsManagementUiState(),
        started = SharingStarted.Lazily,
    )

    private val _addOrEditGarageLevel = MutableStateFlow<GarageLevelUpsertMode?>(null)
    private val _garageLevelTitle = MutableStateFlow("")
    private val _garageLevelSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    private val _newSpotName = MutableStateFlow("")
    private val _detailsLoading = MutableStateFlow(false)

    val garageLevelDetailsState = _addOrEditGarageLevel
        .onEach(::setInitialGarageLevelDetailsState)
        .flatMapLatest { upsertMode ->
            if (upsertMode == null) {
                flowOf(null)
            } else {
                combine(
                    _garageLevelTitle,
                    _garageLevelSpots,
                    _newSpotName,
                    _detailsLoading,
                    garageLevelNamesFlow(upsertMode),
                    uiMapper::toDetailsUiState,
                )
            }
        }
        .stateIn(
            scope = scope,
            initialValue = null,
            started = SharingStarted.Lazily,
        )

    init {
        observeScreenUnavailability(ParkingDashboardOption.SlotsManagementOption.requiredRole)
            .onEach { _routeToNavigate.update { Routes.parkingReservation } }
            .launchIn(scope)
    }

    private fun setInitialGarageLevelDetailsState(mode: GarageLevelUpsertMode?) {
        when (mode) {
            null -> return
            is GarageLevelUpsertMode.New -> {
                _garageLevelTitle.update { "" }
                _garageLevelSpots.update { emptyList() }
                _newSpotName.update { "" }
            }
            is GarageLevelUpsertMode.Edit -> {
                val garageLevelId = mode.garageLevelId
                val garageLevel = state.value.items
                    .firstOrNull { it.id == garageLevelId }
                    ?: return

                _garageLevelTitle.update { garageLevel.title }
                _garageLevelSpots.update { garageLevel.spots.map(ParkingSpotUi::toParkingSpot) }
            }
        }
    }

    fun onEvent(event: SlotsManagementEvent) {
        when (event) {
            is SlotsManagementEvent.PathClick -> _routeToNavigate.update { event.path }
            is SlotsManagementEvent.OnDeleteClick -> _garageLevelForDeletion.update { event.garageLevelDataUi }
            is SlotsManagementEvent.OnAddOrEditClick -> _addOrEditGarageLevel.update {
                when (event.garageLevelId) {
                    null -> GarageLevelUpsertMode.New
                    else -> GarageLevelUpsertMode.Edit(event.garageLevelId)
                }
            }
            is SlotsManagementEvent.OnDeleteSpotClick -> _garageLevelSpots.update {
                it.filter { spot -> spot.id != event.parkingSpotUi.id }
            }
            is SlotsManagementEvent.OnGarageTitleChanged -> _garageLevelTitle.updateInputField(event.text)
            is SlotsManagementEvent.OnNewSpotNameChanged -> _newSpotName.updateInputField(event.text)
            is SlotsManagementEvent.OnSaveClick -> event.handleSaveClick()
            SlotsManagementEvent.DetailsClosed -> clearDetailsData()
            SlotsManagementEvent.OnAddNewSpotClick -> handleAddNewSpot()
            SlotsManagementEvent.OnNegativeClickDeleteDialog -> {
                _uiStateFlags.update { it.copy(closeDeleteDialog = true) }
            }
            SlotsManagementEvent.OnPositiveClickDeleteDialog -> handleOnPositiveClickDeleteDialog()
            SlotsManagementEvent.DeleteDialogClosed -> clearDetailsData()
        }
    }

    private fun SlotsManagementEvent.OnSaveClick.handleSaveClick() {
        scope.launch {
            val upsertMode = _addOrEditGarageLevel.value ?: return@launch
            _detailsLoading.update { true }

            val result = when (upsertMode) {
                is GarageLevelUpsertMode.Edit -> handleUpdateGarageLevel(upsertMode.garageLevelId)
                GarageLevelUpsertMode.New -> handleCreateGarageLevel(title, spots)
            }

            _detailsLoading.update { false }
            _uiStateFlags.update { it.copy(closeAddOrEditDialog = true) }

            _alertMessage.update {
                alertMessageMapper.map(
                    isSuccess = result.isRight(),
                    successMessage = when (upsertMode) {
                        is GarageLevelUpsertMode.Edit -> dictionary.get(StringRes.slotsManagementRequestEditSuccess)
                        GarageLevelUpsertMode.New -> dictionary.get(StringRes.slotsManagementRequestAddSuccess)
                    },
                    failureMessage = dictionary.get(StringRes.slotsManagementRequestFailure),
                )
            }
            _alertMessage.clearAfterDelay()
        }
    }

    private suspend fun handleCreateGarageLevel(
        title: String,
        spots: List<ParkingSpotUi>,
    ) = createGarageLevel(
        request = GarageLevelRequest(
            garageLevel = GarageLevel(
                id = UUID(),
                title = title.trim(),
            ),
            spots = spots.map(ParkingSpotUi::toParkingSpot),
        ),
    )

    private suspend fun handleUpdateGarageLevel(garageLevelId: String) = updateGarageLevel(
        garageLevelData = GarageLevelData(
            id = garageLevelId,
            level = GarageLevel(
                id = garageLevelId,
                title = garageLevelDetailsState.value?.title?.trim().orEmpty(),
            ),
            spots = garageLevelDetailsState.value?.spots.orEmpty().map(ParkingSpotUi::toParkingSpot),
        ),
    )

    private fun handleAddNewSpot() {
        _garageLevelSpots.update {
            it + ParkingSpot(
                id = UUID(),
                title = _newSpotName.value,
            )
        }
        _newSpotName.update { "" }
    }

    private fun handleOnPositiveClickDeleteDialog() {
        scope.launch {
            _uiStateFlags.update {
                it.copy(isDeleteActionInProgress = true)
            }
            val result = _garageLevelForDeletion.value?.let { deleteGarageLevel(it.id) }
            _uiStateFlags.update {
                it.copy(closeDeleteDialog = true)
            }

            _alertMessage.update {
                alertMessageMapper.map(
                    isSuccess = result?.isRight() ?: false,
                    successMessage = dictionary.get(
                        StringRes.slotsManagementRequestDeleteSuccess,
                        _garageLevelForDeletion.value?.title.orEmpty(),
                    ),
                    failureMessage = dictionary.get(StringRes.slotsManagementRequestDeleteFailure),
                )
            }
            _alertMessage.clearAfterDelay()
        }
    }

    private fun clearDetailsData() {
        _addOrEditGarageLevel.update { null }
        _garageLevelTitle.update { "" }
        _garageLevelSpots.update { emptyList() }
        _newSpotName.update { "" }
        _detailsLoading.update { false }
        _uiStateFlags.update {
            it.copy(
                closeAddOrEditDialog = false,
                closeDeleteDialog = false,
                isDeleteActionInProgress = false,
            )
        }
    }

    private fun garageLevelNamesFlow(upsertMode: GarageLevelUpsertMode) = state.map {
        it.items
            .run {
                when (upsertMode) {
                    is GarageLevelUpsertMode.Edit -> filter { garageLevel -> garageLevel.id != upsertMode.garageLevelId }
                    GarageLevelUpsertMode.New -> this
                }
            }.map(GarageLevelDataUi::title)
    }

    private fun MutableStateFlow<String>.updateInputField(input: String) {
        checkInputFieldLength(input).onRight {
            update { input }
        }
    }
}
