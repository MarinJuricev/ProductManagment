package org.product.inventory.web.pages.usersrequests

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
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
import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.components.clearAfterDelay
import org.product.inventory.web.components.toLocalDate
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.core.DateRange
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.datetime.isBeforeOrEqual
import org.product.inventory.web.datetime.millis
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi
import org.product.inventory.web.pages.myreservations.toParkingReservationStatus
import org.product.inventory.web.pages.myreservations.toParkingReservationStatusUi
import parking.dashboard.model.ParkingDashboardOption
import parking.reservation.GetEmptyParkingSpots
import parking.reservation.model.ParkingReservation
import parking.role.ObserveScreenUnavailability
import parking.usersRequests.ObserveUserRequests
import parking.usersRequests.UpdateUserRequest
import user.usecase.GetExistingUser

class UserRequestsViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: UserRequestsUiMapper,
    private val observeUserRequests: ObserveUserRequests,
    private val updateUserRequest: UpdateUserRequest,
    private val getEmptyParkingSpots: GetEmptyParkingSpots,
    private val alertMessageMapper: AlertMessageMapper,
    private val getExistingUser: GetExistingUser,
    private val dictionary: Dictionary,
    observeScreenUnavailability: ObserveScreenUnavailability,
) {

    private val _headerData = MutableStateFlow(UserRequestsHeaderData())

    private val _userRequests = _headerData
        .map { it.dateRange }
        .distinctUntilChanged()
        .onEach { _isLoading.update { true } }
        .flatMapLatest {
            observeUserRequests(
                startDate = it.fromDate.millis,
                endDate = it.toDate.millis,
            )
        }
        .onEach { _isLoading.update { false } }

    private val userRequestsFiltered = combine(
        _userRequests,
        _headerData.map { it.filterData }.distinctUntilChanged(),
        ::filterUserRequests,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Companion.Lazily,
        initialValue = emptyList(),
    )

    private val _selectedUserRequest = MutableStateFlow<ParkingReservation?>(null)
    private val _editableFields = MutableStateFlow(EditableFields())
    private val _savingActive = MutableStateFlow(false)
    private val _closeDialog = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val garageLevelsAndParkingSpotsFetchTrigger = Channel<Unit>()

    private val garageLevelsAndParkingSpots = combine(
        _selectedUserRequest.filterNotNull(),
        garageLevelsAndParkingSpotsFetchTrigger.receiveAsFlow().onStart { emit(Unit) },
    ) { request, _ -> request }.flatMapLatest(::garageLevelAndParkingSpotsStatusFlow)

    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)

    val detailsState: StateFlow<UserRequestDetailsState?> = _selectedUserRequest
        .flatMapLatest(::detailsRequestFlow)
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    val state = core.utils.combine(
        userRequestsFiltered,
        _headerData,
        _closeDialog,
        _isLoading,
        _alertMessage,
        _routeToNavigate,
        uiMapper::buildUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = UserRequestsState(),
    )

    init {
        observeScreenUnavailability(ParkingDashboardOption.UserRequestsOption.requiredRole)
            .onEach { _routeToNavigate.update { Routes.parkingReservation } }
            .launchIn(scope)
    }

    fun onEvent(event: UserRequestsEvent) {
        when (event) {
            is UserRequestsEvent.DateRangeChanged -> updateDateRange(event)
            is UserRequestsEvent.StatusChanged -> event.status?.let { status ->
                _editableFields.update { it.copy(statusUi = status) }
            }
            is UserRequestsEvent.ItemClick -> selectUserRequest(event.itemId)

            UserRequestsEvent.SaveClick -> scope.launch { updateRequest() }
            UserRequestsEvent.ReFetchGarageLevelsClick -> scope.launch {
                garageLevelsAndParkingSpotsFetchTrigger.send(
                    Unit,
                )
            }

            is UserRequestsEvent.ApproveNoteChanged -> _editableFields.update { it.copy(approveNote = event.text) }
            is UserRequestsEvent.RejectReasonChanged -> _editableFields.update {
                it.copy(
                    rejectReason = event.text,
                )
            }

            is UserRequestsEvent.GarageLevelChanged -> updateGarageAndParkingSelection(event)
            is UserRequestsEvent.ParkingSpotChanged -> _editableFields.update { it.copy(parkingSpot = event.parkingOption) }
            UserRequestsEvent.DetailsClosed -> clearDetailsData()
            is UserRequestsEvent.PathClick -> _routeToNavigate.update { event.path }
            UserRequestsEvent.TogglePermanentGarageAccess -> _editableFields.update {
                it.copy(hasPermanentGarageAccess = !it.hasPermanentGarageAccess)
            }

            is UserRequestsEvent.EmailFilterChanged -> _headerData.update { it.copy(filterData = it.filterData.copy(userEmail = event.email)) }
            is UserRequestsEvent.StatusFilterChanged -> _headerData.update { it.copy(filterData = it.filterData.copy(status = event.status)) }
        }
    }

    private fun selectUserRequest(itemId: String) {
        val selectedItem = userRequestsFiltered.value.firstOrNull { it.id == itemId } ?: return
        _selectedUserRequest.update { selectedItem }
    }

    private fun updateGarageAndParkingSelection(event: UserRequestsEvent.GarageLevelChanged) {
        _editableFields.update {
            if (it.garageLevel == event.parkingOption) {
                it
            } else {
                it.copy(
                    garageLevel = event.parkingOption,
                    parkingSpot = ParkingOption.EMPTY,
                )
            }
        }
    }

    private fun clearDetailsData() {
        _editableFields.update { EditableFields() }
        _selectedUserRequest.update { null }
        _savingActive.update { false }
        _closeDialog.update { false }
    }

    private suspend fun updateRequest() {
        val selectedItem = (detailsState.value as? UserRequestDetailsState.UserRequestDetailsItemUi) ?: return
        val itemFromList = userRequestsFiltered.value.firstOrNull { it.id == selectedItem.id } ?: return

        _savingActive.update { true }

        val updatedStatus = when (selectedItem.status) {
            is ParkingReservationStatusUi.Canceled -> selectedItem.status
            is ParkingReservationStatusUi.Submitted -> selectedItem.status
            is ParkingReservationStatusUi.Approved -> selectedItem.status.copy(
                adminNote = selectedItem.approveNote,
                garageLevel = selectedItem.parkingSelectionData.selectedGarageLevel,
                parkingSpot = selectedItem.parkingSelectionData.selectedParkingSpot,
            )

            is ParkingReservationStatusUi.Rejected -> selectedItem.status.copy(
                adminNote = selectedItem.rejectReason,
            )
        }

        val updatedItem = itemFromList.copy(
            status = updatedStatus.toParkingReservationStatus(),
            note = selectedItem.note,
        )

        val result = updateUserRequest(updatedItem)
        _alertMessage.update { alertMessageMapper.map(result.isRight()) }
        _closeDialog.update { true }
        _alertMessage.clearAfterDelay()
    }

    private fun updateDateRange(event: UserRequestsEvent.DateRangeChanged) {
        _headerData.update {
            val fromDate = event.fromDate?.toLocalDate() ?: it.dateRange.fromDate
            val toDate = event.toDate?.toLocalDate() ?: it.dateRange.toDate

            it.copy(
                dateRange = DateRange(
                    fromDate = fromDate,
                    toDate = if (toDate.isBeforeOrEqual(fromDate)) fromDate else toDate,
                ),
            )
        }
    }

    private fun garageLevelAndParkingSpotsStatusFlow(requestItemUi: ParkingReservation) = flow {
        emit(GarageLevelAndParkingSpotsStatus.Loading)

        val result = getEmptyParkingSpots(
            date = requestItemUi.date,
            userRequestId = requestItemUi.id,
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

    private fun detailsRequestFlow(selectedUserRequest: ParkingReservation?) = flow {
        if (selectedUserRequest == null) {
            emit(null)
            return@flow
        }

        emit(UserRequestDetailsState.Loading)

        val statusUi = selectedUserRequest.status.toParkingReservationStatusUi(dictionary)
        val requestApproved = statusUi is ParkingReservationStatusUi.Approved
        val hasPermanentGarageLevelAccess = requestApproved || getPermanentGarageAccessForUser(selectedUserRequest.email)

        // fill editableFields with initial data so validation in mapper can be done
        _editableFields.update {
            EditableFields(
                statusUi = statusUi,
                garageLevel = (statusUi as? ParkingReservationStatusUi.Approved)?.garageLevel,
                parkingSpot = (statusUi as? ParkingReservationStatusUi.Approved)?.parkingSpot,
                approveNote = (statusUi as? ParkingReservationStatusUi.Approved)?.adminNote,
                rejectReason = (statusUi as? ParkingReservationStatusUi.Rejected)?.adminNote,
                hasPermanentGarageAccess = hasPermanentGarageLevelAccess,
            )
        }

        val detailsItemFlow = combine(
            _editableFields,
            garageLevelsAndParkingSpots,
            _savingActive,
        ) { editableFields, garageLevelsAndParkingSpots, savingActive ->
            uiMapper.buildUserRequestDetailsItem(
                parkingReservation = selectedUserRequest,
                editableFields = editableFields,
                garageLevelsAndParkingSpotsStatus = garageLevelsAndParkingSpots,
                id = selectedUserRequest.id,
                savingActive = savingActive,
                permanentGarageAccess = hasPermanentGarageLevelAccess,
            )
        }

        emitAll(detailsItemFlow)
    }

    private suspend fun getPermanentGarageAccessForUser(userEmail: String) =
        getExistingUser(userEmail).getOrNull()?.hasPermanentGarageAccess ?: false

    private fun filterUserRequests(
        userRequests: List<ParkingReservation>,
        filterData: FilterData,
    ): List<ParkingReservation> = userRequests.filter { request ->
        request.email.contains(filterData.userEmail, ignoreCase = true) &&
            when (filterData.status) {
                null -> true
                else -> request.status.toParkingReservationStatusUi(dictionary).text == filterData.status.text
            }
    }
}
