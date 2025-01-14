package org.product.inventory.web.pages.seatreservationtmanagement

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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
import seatreservation.CreateOffice
import seatreservation.DeleteOffice
import seatreservation.ObserveOffices
import seatreservation.UpdateOffice
import seatreservation.model.Office

class SeatReservationManagementViewModel(
    private val scope: CoroutineScope,
    private val deleteOffice: DeleteOffice,
    private val createOffice: CreateOffice,
    private val updateOffice: UpdateOffice,
    private val alertMessageMapper: AlertMessageMapper,
    private val dictionary: Dictionary,
    private val uiMapper: SeatReservationManagementUiMapper,
    observeOffices: ObserveOffices,
) {

    private val _editableFields = MutableStateFlow(SeatReservationManagementEditableFields())
    private val _events = MutableStateFlow(SeatReservationManagementEvents())
    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)
    private val offices = observeOffices()
        .onEach { _events.update { it.copy(isLoading = false) } }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    private val officeNameAlreadyExistsFlow = combine(
        offices,
        _editableFields.map { it.newOfficeName },
    ) { allOffices, newOfficeName ->
        officeNameAlreadyExists(
            allOffices = allOffices,
            officeId = null,
            officeTitle = newOfficeName,
        )
    }

    val state = combine(
        offices,
        _editableFields,
        _events,
        _alertMessage,
        officeNameAlreadyExistsFlow,
        uiMapper::toUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = SeatReservationManagementUiState(),
    )

    private val _selectedDeleteOffice = MutableStateFlow<OfficeUI?>(null)
    val deleteOfficeData = _selectedDeleteOffice
        .map { office -> office?.let(uiMapper::toDeleteOfficeData) }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    private val _editSubmitActive = MutableStateFlow(false)
    private val _selectedEditOffice = MutableStateFlow<OfficeUI?>(null)

    val officeDetailsData = combine(
        _selectedEditOffice.onEach { fillEditTempData(it) }.map { it?.id },
        _editableFields,
        _editSubmitActive,
        offices,
        ::buildOfficeDetailsData,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )

    fun onEvent(event: SeatReservationManagementEvent) {
        when (event) {
            is SeatReservationManagementEvent.PathClick -> _events.update { it.copy(routeToNavigate = event.path) }
            is SeatReservationManagementEvent.DeleteOfficeClick -> _selectedDeleteOffice.update { event.office }
            is SeatReservationManagementEvent.DeleteOfficeConfirmClick -> scope.launch { handleOfficeDelete(_selectedDeleteOffice.value?.id ?: return@launch) }
            SeatReservationManagementEvent.DeleteOfficeCancelClick -> _events.update { it.copy(closeDeleteDialog = true) }
            SeatReservationManagementEvent.DeleteDialogClosed -> {
                _events.update { it.copy(closeDeleteDialog = false) }
                _selectedDeleteOffice.update { null }
            }
            is SeatReservationManagementEvent.NewOfficeNameChange -> _editableFields.update { it.copy(newOfficeName = event.name) }
            is SeatReservationManagementEvent.NewOfficeNumberOfSeatsChange ->
                _editableFields.update { it.copy(newOfficeNumberOfSeats = transformOfficeNumberOfSeatsText(event.numberOfSeats)) }
            is SeatReservationManagementEvent.AddOfficeClick -> scope.launch { handleAddOffice(event.name, event.numberOfSeats) }
            is SeatReservationManagementEvent.EditOfficeClick -> _selectedEditOffice.update { event.office }
            is SeatReservationManagementEvent.EditOfficeConfirmClick -> scope.launch { handleEditOffice(event.data) }
            is SeatReservationManagementEvent.EditOfficeNameChange -> _editableFields.update { it.copy(editOfficeName = event.name) }
            is SeatReservationManagementEvent.EditOfficeNumberOfSeatsChange -> _editableFields.update { it.copy(editOfficeNumberOfSeats = transformOfficeNumberOfSeatsText(event.numberOfSeats)) }
            SeatReservationManagementEvent.EditDialogClosed -> {
                _selectedEditOffice.update { null }
                _events.update { it.copy(closeEditDialog = false) }
            }
        }
    }

    private fun buildOfficeDetailsData(
        officeId: String?,
        editableFields: SeatReservationManagementEditableFields,
        editSubmitActive: Boolean,
        allOffices: List<Office>,
    ): OfficeDetailsData? = when (officeId) {
        null -> null
        else -> uiMapper.toOfficeDetailsData(
            officeId = officeId,
            officeName = editableFields.editOfficeName,
            officeNumberOfSeats = editableFields.editOfficeNumberOfSeats,
            submitActive = editSubmitActive,
            officeNameAlreadyExists = officeNameAlreadyExists(
                allOffices = allOffices,
                officeId = officeId,
                officeTitle = editableFields.editOfficeName,
            ),
        )
    }

    private fun fillEditTempData(officeUI: OfficeUI?) {
        _editableFields.update {
            it.copy(
                editOfficeName = officeUI?.name.orEmpty(),
                editOfficeNumberOfSeats = officeUI?.numberOfSeats.orEmpty(),
            )
        }
    }

    private suspend fun handleEditOffice(editData: OfficeDetailsData) {
        _editSubmitActive.update { true }

        val updateResult = updateOffice(
            office = Office(
                id = editData.id,
                title = editData.name.trim(),
                numberOfSeats = editData.numberOfSeats.trim().toInt(),
            ),
        )

        _editSubmitActive.update { false }
        _events.update { it.copy(closeEditDialog = true) }

        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = updateResult.isRight(),
                successMessage = dictionary.get(StringRes.seatReservationManagementEditOfficeSuccess),
                failureMessage = dictionary.get(StringRes.seatReservationManagementEditOfficeError),
            )
        }

        _alertMessage.clearAfterDelay()
    }

    private suspend fun handleAddOffice(
        officeName: String,
        officeNumberOfSeats: String,
    ) {
        _events.update { it.copy(submitActive = true) }

        val createOfficeResult = createOffice(
            name = officeName.trim(),
            seats = officeNumberOfSeats.trim(),
        )

        _events.update { it.copy(submitActive = false) }

        createOfficeResult.onRight {
            _editableFields.update { it.copy(newOfficeName = "", newOfficeNumberOfSeats = "") }
        }

        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = createOfficeResult.isRight(),
                successMessage = dictionary.get(StringRes.seatReservationManagementAddOfficeSuccess),
                failureMessage = dictionary.get(StringRes.seatReservationManagementAddOfficeError),
            )
        }

        _alertMessage.clearAfterDelay()
    }

    private suspend fun handleOfficeDelete(officeId: String) {
        val office = state.value.offices
            .firstOrNull { it.id == officeId }
            ?.toOffice()
            ?: return

        _events.update { it.copy(deleteInProgress = true) }

        val deleteResult = deleteOffice(office)

        _events.update { it.copy(deleteInProgress = false, closeDeleteDialog = true) }

        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = deleteResult.isRight(),
                successMessage = dictionary.get(StringRes.seatReservationManagementDeleteOfficeSuccess),
                failureMessage = dictionary.get(StringRes.seatReservationManagementDeleteOfficeError),
            )
        }

        _alertMessage.clearAfterDelay()
    }

    private fun transformOfficeNumberOfSeatsText(text: String) = text
        .filter(Char::isDigit)
        .take(10)
        .toIntOrNull()
        ?.takeIf { it != 0 }
        ?.toString()
        .orEmpty()

    private fun officeNameAlreadyExists(
        allOffices: List<Office>,
        officeId: String?,
        officeTitle: String,
    ) = allOffices.any { office ->
        office.id != officeId && office.title.equals(officeTitle, ignoreCase = true)
    }
}
