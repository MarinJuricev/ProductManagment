package seat.timeline

import arrow.core.Either
import auth.Authentication
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import seat.timeline.interaction.OfficeItem
import seat.timeline.interaction.ReservableDateUi
import seat.timeline.interaction.SeatReservationTimelineEvent
import seat.timeline.interaction.SeatReservationTimelineEvent.OfficeChanged
import seat.timeline.interaction.SeatReservationTimelineEvent.OnOptionClick
import seat.timeline.interaction.SeatReservationTimelineEvent.OnRetryClick
import seat.timeline.interaction.SeatReservationTimelineScreenState.Loading
import seat.timeline.interaction.SeatReservationTimelineScreenState.Retry
import seat.timeline.mapper.SeatReservationTimelineUiMapper
import seat.timeline.model.InventoryUserUi
import seat.timeline.model.ReservableDateUiItemOption.Cancel
import seat.timeline.model.ReservableDateUiItemOption.Reserve
import seatreservation.CancelSeatReservation
import seatreservation.GetOffices
import seatreservation.ObserveReservableDates
import seatreservation.ReserveSeat
import seatreservation.model.Office

class SeatReservationTimelineViewModel(
    private val getOffices: GetOffices,
    private val observeReservableDates: ObserveReservableDates,
    private val authentication: Authentication,
    private val uiMapper: SeatReservationTimelineUiMapper,
    private val cancelSeatReservation: CancelSeatReservation,
    private val reserveSeat: ReserveSeat,
) : ScreenModel {

    private val _availableOffices = MutableStateFlow<List<OfficeItem>>(listOf(OfficeItem.Loading))
    private val _selectedOffice = MutableStateFlow<OfficeItem?>(null)
    private val _currentUser = MutableStateFlow<InventoryUserUi>(InventoryUserUi.Loading)
    private val _reservableDates = MutableStateFlow<ReservableDateUi>(ReservableDateUi.Loading)
    private var observeJob: Job? = null

    val uiState = combine(
        _availableOffices,
        _selectedOffice,
        _currentUser,
        _reservableDates,
    ) { availableOffices, selectedOffice, currentUser, reservableDates ->
        when (currentUser) {
            is InventoryUserUi.Error -> Retry
            is InventoryUserUi.Loading -> Loading
            is InventoryUserUi.User -> {
                uiMapper.map(
                    availableOffices = availableOffices,
                    selectedOffice = selectedOffice,
                    selectedUser = currentUser.user,
                    reservableDates = reservableDates,
                )
            }
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Loading,
    )

    init {
        fetchScreenData()
    }

    fun onEvent(event: SeatReservationTimelineEvent) {
        when (event) {
            is OfficeChanged -> handleOfficeChanged(event)
            is OnRetryClick -> fetchScreenData()
            is OnOptionClick -> handleItemOptionClick(event)
        }
    }

    private fun handleItemOptionClick(
        event: OnOptionClick,
    ) = screenModelScope.launch {
        when (val option = event.option) {
            is Cancel -> cancelSeatReservation(officeId = option.officeId, date = option.date)
            is Reserve -> reserveSeat(officeId = option.officeId, date = option.date)
        }
    }

    private fun fetchScreenData() = screenModelScope.launch {
        _currentUser.update { InventoryUserUi.Loading }
        when (val result = authentication.getCurrentUser()) {
            is Either.Left -> _currentUser.update { InventoryUserUi.Error }
            is Either.Right -> {
                _currentUser.update { InventoryUserUi.User(result.value) }
                fetchOffices()
            }
        }
    }

    private fun handleOfficeChanged(event: OfficeChanged) {
        _selectedOffice.update { event.office }
        if (event.office is OfficeItem.OfficeData) {
            startObserveReservableDates(event.office.office)
        }
    }

    private fun fetchOffices() = screenModelScope.launch {
        _availableOffices.update { listOf(OfficeItem.Loading) }
        when (val result = getOffices()) {
            is Either.Left -> {
                _availableOffices.update { listOf(OfficeItem.Undefined) }
            }

            is Either.Right -> {
                val offices = result.value
                _availableOffices.update { offices.map { OfficeItem.OfficeData(it) } }

                offices.firstOrNull()?.let { firstOffice ->
                    _selectedOffice.update { OfficeItem.OfficeData(firstOffice) }
                    startObserveReservableDates(firstOffice)
                }
            }
        }
    }

    private fun startObserveReservableDates(selectedOffice: Office) {
        _reservableDates.update { ReservableDateUi.Loading }
        observeJob?.cancel()
        observeJob = screenModelScope.launch {
            when (val result = observeReservableDates(selectedOffice.id)) {
                is Either.Left -> _reservableDates.update { ReservableDateUi.Error }
                is Either.Right -> {
                    result.value.collect { reservableDates ->
                        _reservableDates.update {
                            ReservableDateUi.Content(
                                reservableDates = reservableDates,
                            )
                        }
                    }
                }
            }
        }
    }
}
