package seat.management

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.role.ObserveScreenUnavailability
import seat.management.components.OfficeItemOption.Delete
import seat.management.components.OfficeItemOption.Edit
import seat.management.interaction.SeatManagementScreenEvent
import seat.management.interaction.SeatManagementScreenEvent.AddOfficeClick
import seat.management.interaction.SeatManagementScreenEvent.DeleteOfficeCanceled
import seat.management.interaction.SeatManagementScreenEvent.DeleteOfficeConfirmed
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeCancelClick
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeNameChanged
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeSaveClick
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeSeatsChanged
import seat.management.interaction.SeatManagementScreenEvent.OfficeNameChange
import seat.management.interaction.SeatManagementScreenEvent.OnOfficeOptionClick
import seat.management.interaction.SeatManagementScreenEvent.SeatsChanged
import seat.management.interaction.SeatManagementScreenState
import seat.management.interaction.SeatManagementViewEffect
import seat.management.interaction.SeatManagementViewEffect.OpenTimeline
import seat.management.interaction.SeatManagementViewEffect.ShowMessage
import seat.management.mapper.DeleteOfficeDialogMapper
import seat.management.mapper.EditOfficeDialogMapper
import seat.management.mapper.SeatManagementScreenUiMapper
import seat.management.mapper.SeatManagementTextMapper
import seat.management.model.SeatManagementDialogs
import seatreservation.CreateOffice
import seatreservation.DeleteOffice
import seatreservation.ObserveOffices
import seatreservation.UpdateOffice
import seatreservation.dashboard.model.SeatReservationOption.SeatManagement
import seatreservation.model.Office

class SeatManagementViewModel(
    private val observeScreenUnavailability: ObserveScreenUnavailability,
    private val observeOfficesData: ObserveOffices,
    private val uiMapper: SeatManagementScreenUiMapper,
    private val createOffice: CreateOffice,
    private val deleteOfficeDialogMapper: DeleteOfficeDialogMapper,
    private val deleteOffice: DeleteOffice,
    private val dictionary: Dictionary,
    private val editOfficeDialogMapper: EditOfficeDialogMapper,
    private val updateOffice: UpdateOffice,
    textMapper: SeatManagementTextMapper,
) : ScreenModel {

    private val _viewEffect = Channel<SeatManagementViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<SeatManagementViewEffect> = _viewEffect.receiveAsFlow()
    private val uiTexts = MutableStateFlow(textMapper.map())
    private val _offices = MutableStateFlow<List<Office>>(emptyList())
    private val _officeName = MutableStateFlow("")
    private val _dialogs = MutableStateFlow(SeatManagementDialogs())
    private val _seats = MutableStateFlow<Int?>(null)
    private var selectedOffice: Office? = null

    init {
        checkScreenUnavailability()
        observeOffices()
    }

    private fun checkScreenUnavailability() =
        observeScreenUnavailability(SeatManagement.requiredRole)
            .onEach { _viewEffect.send(OpenTimeline) }
            .launchIn(screenModelScope)

    private fun observeOffices() = screenModelScope.launch {
        observeOfficesData().collect { offices ->
            _offices.update { offices }
        }
    }

    val uiState = combine(
        uiTexts,
        _offices,
        _officeName,
        _seats,
        _dialogs,
        uiMapper::map,
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SeatManagementScreenState.Loading,
    )

    fun onEvent(event: SeatManagementScreenEvent) {
        when (event) {
            is OfficeNameChange -> _officeName.update { event.name }
            is SeatsChanged -> _seats.update { event.seats.toIntOrNull() }
            is DeleteOfficeCanceled -> _dialogs.update { it.copy(deleteDialogData = null) }
            is DeleteOfficeConfirmed -> executeDeleteOffice()
            is AddOfficeClick -> saveNewOffice(event.name, event.seats)
            is OnOfficeOptionClick -> handleOnOfficeOptionClick(event)
            is EditOfficeCancelClick -> _dialogs.update { it.copy(editOfficeDialogData = null) }
            is EditOfficeNameChanged -> handleOfficeNameChanged(event)
            is EditOfficeSaveClick -> handleOfficeSaveClick(event)
            is EditOfficeSeatsChanged -> handleOfficeSeatsChanged(event)
        }
    }

    private fun handleDeleteOfficeClick(office: Office) {
        selectedOffice = office
        _dialogs.update { it.copy(deleteDialogData = deleteOfficeDialogMapper.map(office)) }
    }

    private fun executeDeleteOffice() = screenModelScope.launch {
        _dialogs.update { it.copy(deleteDialogData = null) }
        selectedOffice?.let { deleteOffice(it) }
    }

    private fun saveNewOffice(
        name: String,
        seats: String,
    ) = screenModelScope.launch {
        _seats.update { null }
        _officeName.update { "" }
        createOffice(name, seats).onLeft {
            _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.seat_reservation_office_creation_error)))
        }
    }

    private fun handleOnOfficeOptionClick(event: OnOfficeOptionClick) {
        when (event.option) {
            is Delete -> handleDeleteOfficeClick(event.office)
            is Edit -> handelEditOfficeClick(event.office)
        }
    }

    private fun handelEditOfficeClick(office: Office) {
        _dialogs.update { it.copy(editOfficeDialogData = editOfficeDialogMapper.map(office)) }
    }

    private fun handleOfficeNameChanged(event: EditOfficeNameChanged) {
        _dialogs.update {
            it.copy(
                editOfficeDialogData = editOfficeDialogMapper.map(
                    office = event.office,
                    officeName = event.name,
                ),
            )
        }
    }

    private fun handleOfficeSaveClick(event: EditOfficeSaveClick) = screenModelScope.launch {
        _dialogs.update { it.copy(editOfficeDialogData = null) }
        val result = updateOffice(event.office)
        when (result) {
            is Either.Left -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.general_update_failed)))
            is Either.Right -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.general_updated)))
        }
    }

    private fun handleOfficeSeatsChanged(event: EditOfficeSeatsChanged) {
        _dialogs.update {
            it.copy(
                editOfficeDialogData = editOfficeDialogMapper.map(
                    office = event.office,
                    seats = event.seats,
                ),
            )
        }
    }
}
