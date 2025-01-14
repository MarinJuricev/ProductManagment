package org.product.inventory.web.pages.newrequest

import arrow.core.Either
import core.utils.millisNow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.components.clearAfterDelay
import org.product.inventory.web.components.startOfTheDay
import org.product.inventory.web.components.toDateInputValue
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.pages.Routes
import parking.dashboard.model.ParkingDashboardOption
import parking.reservation.GetEmptyParkingSpots
import parking.reservation.MY_RESERVATIONS_RANGE
import parking.reservation.ManageParkingRequest
import parking.reservation.ObserveMyParkingRequests
import parking.reservation.RequestMultipleParkingPlaces
import parking.reservation.model.GarageLevel
import parking.reservation.model.MultipleParkingRequestState
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingRequest
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingSpot
import parking.role.ObserveScreenUnavailability
import user.model.InventoryAppRole
import user.model.InventoryAppUser
import user.usecase.GetUsers
import user.usecase.ObserveCurrentUser

class NewRequestViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: NewRequestUiMapper,
    private val manageParkingRequest: ManageParkingRequest,
    private val alertMessageMapper: AlertMessageMapper,
    private val dictionary: Dictionary,
    private val getUsers: GetUsers,
    private val getEmptyParkingSpots: GetEmptyParkingSpots,
    private val multiDateSelectionStateMapper: MultiDateSelectionStateMapper,
    private val requestMultipleParkingPlaces: RequestMultipleParkingPlaces,
    observeMyParkingRequests: ObserveMyParkingRequests,
    observeScreenUnavailability: ObserveScreenUnavailability,
    observeCurrentUser: ObserveCurrentUser,
) {

    private val _editableFields = MutableStateFlow(NewRequestEditableFields())
    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)
    private val _events = MutableStateFlow(NewRequestEvents())
    private val _selectedRequestDates: MutableStateFlow<Map<DateInputValue, MultipleParkingRequestState>> = MutableStateFlow(emptyMap())

    private val currentUser = observeCurrentUser().stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )
    private val _selectedUser = MutableStateFlow<UserListItem?>(null)
    private val selectedUser = combine(
        currentUser.filterNotNull()
            .onEach { handleRoleChange(it.role) }
            .map { it.toUserListItem(dictionary) },
        _selectedUser,
    ) { loggedUser, selectedUser -> selectedUser ?: loggedUser }

    private val requestMode = combine(
        currentUser.filterNotNull().map { it.role }
            .distinctUntilChanged()
            .onEach { _editableFields.update { it.copy(notes = "", date = DateInputValue(millisNow(dayOffset = 1))) } },
        selectedUser,
        _selectedRequestDates,
        ::buildRequestMode,
    )

    private val _garageLevelAndParkingSpotsFetchTrigger = Channel<Unit>()
    private val garageLevelAndParkingSpotsStatus = combine(
        _garageLevelAndParkingSpotsFetchTrigger.receiveAsFlow().onStart { emit(Unit) },
        _editableFields.map { it.date.value }.distinctUntilChanged(),
        currentUser.filterNotNull(),
        ::Triple,
    ).filter { (_, _, user) -> user.role.isEligibleForAutomaticRequest() }
        .onEach { resetParkingSelectionData() }
        .flatMapLatest { (_, dateMillis, _) -> garageLevelAndParkingSpotsStatusFlow(dateMillis) }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = GarageLevelAndParkingSpotsStatus.Loading,
        )

    val state = core.utils.combine(
        _editableFields,
        _events,
        _alertMessage,
        selectedUser,
        requestMode,
        garageLevelAndParkingSpotsStatus,
        uiMapper::buildUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = NewRequestState(),
    )

    private val _userListTrigger = Channel<Unit?>()
    val userListState = combine(
        _editableFields.map { it.userListFilterText }.distinctUntilChanged(),
        _userListTrigger.receiveAsFlow().flatMapLatest(::userListStateFlow),
        ::filterListState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )

    val multiDateSelectionState = combine(
        observeMyParkingRequests(
            startDate = millisNow(),
            endDate = millisNow(MY_RESERVATIONS_RANGE),
        ).map { parkingRequests ->
            parkingRequests.map { request -> request.date.toDateInputValue().startOfTheDay() }
        },
        _selectedRequestDates,
        currentUser.filterNotNull(),
        multiDateSelectionStateMapper::buildMultipleSelectionState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = MultiDateSelectionState(),
    )

    init {
        observeScreenUnavailability(ParkingDashboardOption.NewRequestOption.requiredRole)
            .onEach { _events.update { it.copy(routeToNavigate = Routes.parkingReservation) } }
            .launchIn(scope)
    }

    fun onEvent(event: NewRequestEvent) {
        when (event) {
            is NewRequestEvent.NotesChanged -> _editableFields.update { it.copy(notes = event.notes) }
            is NewRequestEvent.DateChanged -> {
                _editableFields.update { it.copy(date = event.date) }
                _events.update { it.copy(isLateReservation = false) }
            }
            NewRequestEvent.SubmitClick -> handleSubmitClick()
            is NewRequestEvent.PathClick -> _events.update { it.copy(routeToNavigate = event.path) }
            NewRequestEvent.ProfileInfoClick -> handleProfileInfoClick()
            is NewRequestEvent.UserListFilterChanged -> _editableFields.update { it.copy(userListFilterText = event.text) }
            is NewRequestEvent.UserListItemClick -> {
                _selectedUser.update { event.userListItem }
                _events.update { it.copy(closeDialog = true) }
            }
            is NewRequestEvent.GarageLevelChanged -> _editableFields.update {
                if (event.garageLevel != it.selectedGarageLevel) {
                    it.copy(
                        selectedGarageLevel = event.garageLevel,
                        selectedParkingSpot = ParkingOption.EMPTY,
                    )
                } else {
                    it
                }
            }
            is NewRequestEvent.ParkingSpotChanged -> _editableFields.update { it.copy(selectedParkingSpot = event.parkingSpot) }
            NewRequestEvent.ReFetchGarageLevelDataClick -> scope.launch { _garageLevelAndParkingSpotsFetchTrigger.send(Unit) }
            NewRequestEvent.ToggleGarageAccess -> _editableFields.update { it.copy(garageAccess = !it.garageAccess) }
            NewRequestEvent.UserListClosed -> resetUserListData()
            is NewRequestEvent.AddRequestDate -> addDateIfDoesNotExists(event.date)
            is NewRequestEvent.RemoveRequestDate -> _selectedRequestDates.update { it - event.date }
        }
    }

    private fun resetUserListData() {
        scope.launch {
            _userListTrigger.send(null)
            _events.update { it.copy(closeDialog = false) }
            _editableFields.update { it.copy(userListFilterText = "") }
        }
    }

    private suspend fun handleShowingAlertMessage(
        isSuccess: Boolean = false,
        failureMessage: String?,
    ) {
        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = isSuccess,
                failureMessage = failureMessage,
            )
        }
        _alertMessage.clearAfterDelay()
    }

    private fun handleProfileInfoClick() {
        if (state.value.requestMode !is RequestMode.Automatic) return
        scope.launch { _userListTrigger.send(Unit) }
    }

    private fun userListStateFlow(trigger: Unit?) = flow {
        if (trigger == null) {
            emit(null)
            return@flow
        }

        emit(UserListDialogState.Loading)

        emit(
            when (val result = getUsers()) {
                is Either.Left -> UserListDialogState.Error(
                    message = dictionary.get(StringRes.parkingReservationNewRequestUserListErrorMessage),
                )
                is Either.Right -> UserListDialogState.Success(
                    users = result.value.map(InventoryAppUser::toUserListItem),
                )
            },
        )
    }

    private fun filterListState(
        filterText: String,
        state: UserListDialogState?,
    ): UserListDialogState? = state?.mapSuccess { users ->
        users.filter { user ->
            user.profileInfo.username.contains(filterText, ignoreCase = true)
        }
    }

    private fun handleSubmitClick() {
        val user = currentUser.value ?: return
        if (user.role == InventoryAppRole.User) {
            submitMultipleNewRequests(user)
        } else {
            submitNewRequest(user)
        }
    }

    private fun submitNewRequest(user: InventoryAppUser) {
        scope.launch {
            _events.update { it.copy(isLoading = true) }
            var failureMessage: String? = null

            val result = manageParkingRequest(buildParkingRequest(user))

            when (result) {
                is Either.Left -> failureMessage = result.value.getFailureMessage()
                is Either.Right -> {
                    _events.update { it.copy(isLateReservation = false) }
                    _editableFields.update { fields ->
                        fields.copy(
                            notes = "",
                            date = DateInputValue(millisNow(dayOffset = 1)),
                        )
                    }
                }
            }

            _events.update { it.copy(isLoading = false) }

            val isLateReservationError = result.leftOrNull() is ParkingReservationError.LateReservation

            if (isLateReservationError) {
                _events.update { it.copy(isLateReservation = true) }
            } else {
                handleShowingAlertMessage(result.isRight(), failureMessage)
            }
        }
    }

    private fun submitMultipleNewRequests(user: InventoryAppUser) {
        scope.launch {
            _events.update { it.copy(isLoading = true) }

            val parkingRequests = _selectedRequestDates.value.filter {
                it.value != MultipleParkingRequestState.SUCCESS
            }.map { (date, _) -> buildParkingRequest(user, date) }

            when (val result = requestMultipleParkingPlaces(parkingRequests)) {
                is Either.Left -> {
                    handleShowingAlertMessage(failureMessage = dictionary.get(StringRes.parkingReservationTooManyRequestsErrorMessage))
                }
                is Either.Right -> {
                    result.value.map { requests ->
                        requests.mapKeys { it.key.toDateInputValue() }
                    }.collectLatest { result ->
                        _selectedRequestDates.update { result }
                    }
                }
            }

            _events.update { it.copy(isLoading = false) }
        }
    }

    private fun buildParkingRequest(
        currentUser: InventoryAppUser,
        parkingDate: DateInputValue? = null,
    ) = with(state.value) {
        when (val mode = requestMode) {
            is RequestMode.Basic -> ParkingRequest.Request(
                email = currentUser.email,
                date = parkingDate?.value ?: date.value,
                note = notes,
            )
            is RequestMode.Automatic -> ParkingRequest.Reservation(
                email = mode.userListItem.profileInfo.email,
                date = date.value,
                adminNote = notes,
                parkingCoordinate = ParkingCoordinate(
                    level = GarageLevel(
                        id = parkingSelectionData.selectedGarageLevel.id,
                        title = parkingSelectionData.selectedGarageLevel.value,
                    ),
                    spot = ParkingSpot(
                        id = parkingSelectionData.selectedParkingSpot.id,
                        title = parkingSelectionData.selectedParkingSpot.value,
                    ),
                ),
            )
        }
    }

    private fun ParkingReservationError.getFailureMessage() = when (this) {
        ParkingReservationError.DuplicateReservation -> dictionary.get(StringRes.parkingReservationDuplicateReservationError)
        ParkingReservationError.LateReservation -> dictionary.get(StringRes.parkingReservationLateReservationError)
        else -> dictionary.get(StringRes.alertMessageFailure)
    }

    private fun buildRequestMode(
        role: InventoryAppRole,
        selectedUser: UserListItem,
        selectedRequestDates: Map<DateInputValue, MultipleParkingRequestState>,
    ) = when (role.isEligibleForAutomaticRequest()) {
        true -> RequestMode.Automatic(selectedUser)
        false -> RequestMode.Basic(selectedRequestDates.isNotEmpty())
    }

    private fun garageLevelAndParkingSpotsStatusFlow(dateMillis: Long) = flow {
        emit(GarageLevelAndParkingSpotsStatus.Loading)

        val result = getEmptyParkingSpots(
            date = dateMillis,
            userRequestId = null,
        )

        emit(
            when (result) {
                is Either.Left -> GarageLevelAndParkingSpotsStatus.Error
                is Either.Right -> GarageLevelAndParkingSpotsStatus.Success(
                    garageLevelData = result.value,
                )
            },
        )
    }

    private fun resetParkingSelectionData() {
        _editableFields.update {
            it.copy(
                selectedGarageLevel = ParkingOption.EMPTY,
                selectedParkingSpot = ParkingOption.EMPTY,
                garageAccess = false,
            )
        }
    }

    private fun handleRoleChange(role: InventoryAppRole) {
        if (!role.isEligibleForAutomaticRequest()) {
            _selectedUser.update { null }
        }
    }

    private fun InventoryAppRole.isEligibleForAutomaticRequest() = when (this) {
        InventoryAppRole.Administrator, InventoryAppRole.Manager -> true
        else -> false
    }

    private fun addDateIfDoesNotExists(date: DateInputValue) {
        _selectedRequestDates.update { currentDates ->
            val multiDateSelectionState = multiDateSelectionState.value
            when {
                multiDateSelectionState.previouslyReservedDates.contains(date.startOfTheDay()) || date in currentDates -> {
                    scope.launch {
                        handleShowingAlertMessage(failureMessage = multiDateSelectionState.staticData.duplicateParkingReservationErrorMessage)
                    }
                    currentDates
                }
                else -> currentDates.toMutableMap().apply {
                    this[date] = MultipleParkingRequestState.LOADING
                }
            }
        }
    }
}
