package parking.usersRequests.details

import arrow.core.Either.Left
import arrow.core.Either.Right
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
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.GetEmptyParkingSpots
import parking.reservation.mapper.ReservationDetailsScreenTextsMapper
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationUiModel
import parking.usersRequests.UpdateUserRequest
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.AdminNoteChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.GarageLevelChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.GarageSpotChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.HasGarageAccessChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.RetryClick
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.SaveButtonClick
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.ScreenShown
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.StatusChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsFieldsValidator
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState.Content
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState.Error
import parking.usersRequests.details.interaction.ParkingReservationDetailsViewEffect
import parking.usersRequests.details.interaction.ParkingReservationDetailsViewEffect.ReservationUpdated
import parking.usersRequests.details.interaction.ParkingReservationDetailsViewEffect.ShowMessage
import parking.usersRequests.details.interaction.SelectedUserState
import parking.usersRequests.details.mapper.ParkingReservationDetailsScreenMapper
import parking.usersRequests.details.mapper.toUiModel
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi.Deselected
import parking.usersRequests.details.model.ReservationDetailsEditableFields
import user.usecase.GetExistingUser

class ParkingReservationDetailsViewModel(
    private val screenMapper: ParkingReservationDetailsScreenMapper,
    private val getParkingSpots: GetEmptyParkingSpots,
    private val fieldsValidator: ParkingReservationDetailsFieldsValidator,
    private val updateUserRequest: UpdateUserRequest,
    private val dictionary: Dictionary,
    private val getUser: GetExistingUser,
    uiTextsMapper: ReservationDetailsScreenTextsMapper,
) : ScreenModel {
    private val _viewEffect = Channel<ParkingReservationDetailsViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<ParkingReservationDetailsViewEffect> = _viewEffect.receiveAsFlow()
    private val _editableFields = MutableStateFlow(ReservationDetailsEditableFields())
    private val _selectedRequest = MutableStateFlow<ParkingReservationUiModel?>(null)
    private val _availableLevels =
        MutableStateFlow<List<GarageLevelUi>>(listOf(GarageLevelUi.Loading))
    private val _selectedUser = MutableStateFlow<SelectedUserState>(SelectedUserState.Loading)
    private val screenTexts = uiTextsMapper.map()
    val uiState = combine(
        _editableFields,
        _selectedRequest,
        _availableLevels,
        _selectedUser,
    ) { editableFields, selectedReservation, availableLevels, selectedUser ->
        selectedReservation?.let {
            screenMapper(
                selectedParkingRequest = selectedReservation,
                editableFields = editableFields,
                availableGarageLevels = availableLevels,
                selectedUser = selectedUser,
                screenTexts = screenTexts,
            )
        } ?: Error
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ParkingReservationDetailsScreenState.Loading,
    )

    fun onEvent(event: ParkingReservationDetailsEvent) {
        when (event) {
            is ScreenShown -> event.handleScreenShown()
            is StatusChanged -> event.handleStatusChange()
            is AdminNoteChanged -> _editableFields.update { it.copy(adminNote = event.note) }
            is GarageLevelChanged -> event.handleLevelChanged()
            is GarageSpotChanged -> _editableFields.update { it.copy(garageSpotUi = event.spot) }
            is SaveButtonClick -> validateFields(event.data)
            is HasGarageAccessChanged -> _editableFields.update { it.copy(hasGarageAccess = event.hasAccess) }
            is RetryClick -> fetchSelectedUser(event.selectedRequest)
        }
    }

    private fun validateFields(data: Content) = screenModelScope.launch {
        when (val validationResult = fieldsValidator(data)) {
            is Left -> _viewEffect.send(ShowMessage(validationResult.value))
            is Right -> updateParkingReservation(data.toParkingReservation())
        }
    }

    private fun updateParkingReservation(data: ParkingReservation) = screenModelScope.launch {
        _editableFields.update { it.copy(saveButtonLoading = true) }
        when (
            val result = updateUserRequest(
                parkingReservation = data,
            )
        ) {
            is Left -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.parking_reservation_failure)))
            is Right -> {
                _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.parking_reservation_success)))
                _viewEffect.send(ReservationUpdated(data))
            }
        }
        _editableFields.update { it.copy(saveButtonLoading = false) }
    }

    private fun ScreenShown.handleScreenShown() {
        _selectedRequest.update { selectedRequest }
        fetchAvailableSpots(selectedRequest)
        fetchSelectedUser(selectedRequest)
    }

    private fun fetchSelectedUser(selectedRequest: ParkingReservationUiModel) =
        screenModelScope.launch {
            _selectedUser.update { SelectedUserState.Loading }
            when (
                val result = getUser(selectedRequest.email)
            ) {
                is Left -> {
                    _viewEffect.send(ShowMessage(result.value.toString()))
                    _selectedUser.update { SelectedUserState.Error }
                }

                is Right -> _selectedUser.update { SelectedUserState.Received(result.value) }
            }
        }

    private fun fetchAvailableSpots(selectedRequest: ParkingReservationUiModel) =
        screenModelScope.launch {
            when (
                val result = getParkingSpots(
                    date = selectedRequest.dateTimeStamp,
                    userRequestId = selectedRequest.id,
                )
            ) {
                is Left -> _viewEffect.send(ShowMessage(result.value.toString()))

                is Right -> _availableLevels.update { result.value.map { it.toUiModel() } }
            }
        }

    private fun StatusChanged.handleStatusChange() {
        _editableFields.update {
            it.copy(
                currentStatus = status.status,
                adminNote = "",
                garageLevelUi = GarageLevelUi.Deselected,
                garageSpotUi = Deselected,
            )
        }
    }

    private fun GarageLevelChanged.handleLevelChanged() {
        _editableFields.update { it.copy(garageLevelUi = level, garageSpotUi = Deselected) }
    }
}
