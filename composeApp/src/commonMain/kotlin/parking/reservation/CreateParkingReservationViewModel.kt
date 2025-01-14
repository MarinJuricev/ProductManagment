package parking.reservation

import arrow.core.Either.Left
import arrow.core.Either.Right
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import core.utils.millisNow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.interaction.CreateParkingReservationEvent
import parking.reservation.interaction.CreateParkingReservationEvent.ChangeUserClick
import parking.reservation.interaction.CreateParkingReservationEvent.DateSelected
import parking.reservation.interaction.CreateParkingReservationEvent.GarageLevelChanged
import parking.reservation.interaction.CreateParkingReservationEvent.GarageSpotChanged
import parking.reservation.interaction.CreateParkingReservationEvent.HasGarageAccessChanged
import parking.reservation.interaction.CreateParkingReservationEvent.MultiDateSelectionAdded
import parking.reservation.interaction.CreateParkingReservationEvent.MultiDateSelectionRemoved
import parking.reservation.interaction.CreateParkingReservationEvent.NoteChanged
import parking.reservation.interaction.CreateParkingReservationEvent.RetryFetchGarageData
import parking.reservation.interaction.CreateParkingReservationEvent.SubmitButtonClick
import parking.reservation.interaction.CreateParkingReservationEvent.UserChanged
import parking.reservation.interaction.CreateParkingReservationScreenState
import parking.reservation.interaction.CreateParkingReservationScreenState.Content
import parking.reservation.interaction.CreateParkingReservationScreenState.Loading
import parking.reservation.interaction.CreateParkingReservationViewEffect
import parking.reservation.interaction.CreateParkingReservationViewEffect.NavigateToDashboard
import parking.reservation.interaction.CreateParkingReservationViewEffect.OpenUserPicker
import parking.reservation.interaction.CreateParkingReservationViewEffect.ShowMessage
import parking.reservation.interaction.GarageDataUI
import parking.reservation.mapper.CreateParkingReservationUiMapper
import parking.reservation.mapper.MultiDateSelectionUiMapper
import parking.reservation.model.GarageLevelsAndSpotsStatus
import parking.reservation.model.GarageLevelsAndSpotsStatus.Failure
import parking.reservation.model.GarageLevelsAndSpotsStatus.Success
import parking.reservation.model.MultiDateSelectionState
import parking.reservation.model.MultipleParkingRequestState
import parking.reservation.model.NewRequestEditableFields
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingRequest
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.RequestMode.Request
import parking.reservation.model.RequestMode.Reservation
import parking.usersRequests.details.mapper.toUiModel
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageLevelUi.GarageLevelUiModel
import parking.usersRequests.details.model.GarageSpotUi
import parking.usersRequests.details.model.GarageSpotUi.GarageSpotUiModel
import user.model.InventoryAppRole
import user.model.InventoryAppRole.Manager
import user.model.InventoryAppUser
import user.usecase.ObserveCurrentUser

class CreateParkingReservationViewModel(
    private val screenMapper: CreateParkingReservationUiMapper,
    private val manageParkingRequest: ManageParkingRequest,
    observeCurrentUser: ObserveCurrentUser,
    private val getEmptyParkingSpots: GetEmptyParkingSpots,
    private val dictionary: Dictionary,
    observeMyParkingRequests: ObserveMyParkingRequests,
    private val multiDateSelectionUiMapper: MultiDateSelectionUiMapper,
    private val requestMultipleParkingPlaces: RequestMultipleParkingPlaces,
) : ScreenModel {

    private val _viewEffect = Channel<CreateParkingReservationViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<CreateParkingReservationViewEffect> = _viewEffect.receiveAsFlow()
    private val _selectedUser = MutableStateFlow<InventoryAppUser?>(null)
    private val _selectedDates = MutableStateFlow<List<Long>>(emptyList())
    private val _editableFields = MutableStateFlow(NewRequestEditableFields())
    private val _garageDataFetchTrigger = Channel<Unit>()
    private val _multipleRequestsStatuses =
        MutableStateFlow<Map<Long, MultipleParkingRequestState>>(emptyMap())

    private val _multiDateSelectionState = combine(
        observeMyParkingRequests(
            startDate = millisNow(),
            endDate = millisNow(MY_RESERVATIONS_OBSERVE_OFFSET),
        ),
        _selectedDates,
        _multipleRequestsStatuses,
        multiDateSelectionUiMapper::map,
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MultiDateSelectionState(),
    )

    private val selectedUser = combine(
        observeCurrentUser().onEach { handleRoleChanged(it.role) },
        _selectedUser,
    ) { loggedUser, selectedUser -> selectedUser ?: loggedUser }

    private fun handleRoleChanged(role: InventoryAppRole) {
        if (role < Manager) _selectedUser.update { null }
    }

    private val requestMode = observeCurrentUser()
        .map { buildRequestMode(it.role) }

    private fun buildRequestMode(
        currentUserRole: InventoryAppRole,
    ) = when {
        currentUserRole >= Manager -> Reservation
        else -> Request
    }

    private val _garageLevelsAndSpotsStatus = combine(
        _garageDataFetchTrigger.receiveAsFlow().onStart { emit(Unit) },
        _editableFields.map { it.selectedDate }.distinctUntilChanged(),
        observeCurrentUser(),
        ::Triple,
    ).filter { (_, _, user) -> user.role >= Manager }
        .onEach { clearEnteredGarageData() }
        .flatMapLatest { (_, date, _) -> garageLevelAndParkingSpotsStatusFlow(date) }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = GarageLevelsAndSpotsStatus.Loading,
        )

    private fun clearEnteredGarageData() {
        _editableFields.update {
            it.copy(
                selectedGarageLevel = GarageLevelUi.Deselected,
                selectedGarageSpot = GarageSpotUi.Deselected,
                hasGarageAccess = false,
            )
        }
    }

    private fun garageLevelAndParkingSpotsStatusFlow(dateMillis: Long) = flow {
        emit(GarageLevelsAndSpotsStatus.Loading)
        val result = getEmptyParkingSpots(date = dateMillis, userRequestId = null)
        emit(
            when (result) {
                is Left -> Failure
                is Right -> Success(levels = result.value.map { it.toUiModel() })
            },
        )
    }

    val uiState = combine(
        selectedUser,
        requestMode,
        _editableFields,
        _garageLevelsAndSpotsStatus,
        _multiDateSelectionState,
        screenMapper::map,
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Loading,
    )

    fun onEvent(event: CreateParkingReservationEvent) {
        when (event) {
            is DateSelected -> _editableFields.update { it.copy(selectedDate = event.timeStamp) }
            is NoteChanged -> _editableFields.update { it.copy(notes = event.note) }
            is SubmitButtonClick -> determineSubmitStrategy(uiState.value)
            is ChangeUserClick -> openUserPicker(event.currentlySelectedUser)
            is UserChanged -> _selectedUser.update { event.user }
            is HasGarageAccessChanged -> _editableFields.update { it.copy(hasGarageAccess = event.hasAccess) }
            is RetryFetchGarageData -> screenModelScope.launch { _garageDataFetchTrigger.send(Unit) }
            is GarageSpotChanged -> _editableFields.update { it.copy(selectedGarageSpot = event.garageSpotUi) }
            is GarageLevelChanged -> _editableFields.update {
                it.copy(
                    selectedGarageLevel = event.garageLevelUi,
                    selectedGarageSpot = GarageSpotUi.Deselected,
                )
            }

            is MultiDateSelectionAdded -> _selectedDates.update { it + event.selectedDate }
            is MultiDateSelectionRemoved -> _selectedDates.update { it - event.selectedDate }
        }
    }

    private fun determineSubmitStrategy(state: CreateParkingReservationScreenState) =
        screenModelScope.launch {
            if (state is Content) {
                when (state.requestMode) {
                    Request -> submitMultipleRequests(state)
                    Reservation -> submitReservation(state)
                }
            }
        }

    private fun submitMultipleRequests(state: Content) = screenModelScope.launch {
        val requests = state.multiDatePickerState.selectedDates.map { date ->
            ParkingRequest.Request(
                email = state.headerData.user.email,
                date = date.timestamp,
                note = state.notesFormData.notes,
            )
        }
        when (val result = requestMultipleParkingPlaces(requests)) {
            is Left -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.general_unknown_error_title)))
            is Right -> result.value.collectLatest { _multipleRequestsStatuses.value += it }
        }
    }

    private fun submitReservation(state: Content) =
        screenModelScope.launch {
            val parkingCoordinate = extractParkingCoordinate(state)
            parkingCoordinate?.let {
                val request = ParkingRequest.Reservation(
                    email = state.headerData.user.email,
                    date = state.datePickerData.selectedTimestamp,
                    adminNote = state.notesFormData.notes,
                    parkingCoordinate = it,
                )
                _editableFields.update { it.copy(submitButtonLoading = true) }
                val result = manageParkingRequest(request)
                _editableFields.update { it.copy(submitButtonLoading = false) }
                when (result) {
                    is Left -> _viewEffect.send(ShowMessage(result.value.getFailureMessage()))
                    is Right -> {
                        _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.parking_reservation_success)))
                        _viewEffect.send(NavigateToDashboard)
                    }
                }
            }
        }

    private fun extractParkingCoordinate(state: Content): ParkingCoordinate? =
        when (state.garageData) {
            is GarageDataUI.Content -> {
                val levelUi = state.garageData.currentGarageLevel
                val spotUi = state.garageData.currentGarageSpot
                if (levelUi is GarageLevelUiModel && spotUi is GarageSpotUiModel) {
                    ParkingCoordinate(levelUi.garageLevel, spotUi.spot)
                } else {
                    null
                }
            }

            else -> null
        }

    private fun ParkingReservationError.getFailureMessage() = when (this) {
        ParkingReservationError.DuplicateReservation -> dictionary.getString(MR.strings.parking_reservation_duplicate_reservation_error)
        ParkingReservationError.LateReservation -> dictionary.getString(MR.strings.parking_reservation_late_reservation_error)
        else -> dictionary.getString(MR.strings.general_unknown_error_title)
    }

    private fun openUserPicker(currentlySelectedUser: InventoryAppUser) = screenModelScope.launch {
        _viewEffect.send(OpenUserPicker(preselectedUser = currentlySelectedUser))
    }
}

private const val MY_RESERVATIONS_OBSERVE_OFFSET = 30
