package parking.myReservations

import MyReservationsScreenMapper
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import core.utils.endOfTheDay
import core.utils.startOfTheDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import parking.myReservations.interaction.MyReservationsEvent
import parking.myReservations.interaction.MyReservationsEvent.DateRangeSelect
import parking.myReservations.interaction.MyReservationsEvent.ReservationClick
import parking.myReservations.interaction.MyReservationsEvent.ReservationUpdated
import parking.myReservations.interaction.MyReservationsEvent.RetryClick
import parking.myReservations.interaction.MyReservationsUiState
import parking.myReservations.interaction.MyReservationsViewEffect
import parking.reservation.ObserveMyParkingRequests
import parking.reservation.model.ParkingReservationUiModel
import utils.getMillisOfLastDayInCurrentMonth

class MyReservationsScreenViewModel(
    private val observeMyParkingRequests: ObserveMyParkingRequests,
    private val screenMapper: MyReservationsScreenMapper,
) : ScreenModel {

    private val _viewEffect = Channel<MyReservationsViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<MyReservationsViewEffect> = _viewEffect.receiveAsFlow()
    private val _uiState = MutableStateFlow<MyReservationsUiState>(MyReservationsUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private val _selectedReservation = MutableStateFlow<ParkingReservationUiModel?>(null)
    val selectedReservation = _selectedReservation.asStateFlow()
    private var observeJob: Job? = null
    private var selectedStartDate = now().toEpochMilliseconds().startOfTheDay(TimeZone.UTC).toLong()
    private var selectedEndDate = getMillisOfLastDayInCurrentMonth().endOfTheDay().toLong()

    init {
        observeMyReservations(selectedStartDate, selectedEndDate)
    }

    fun onEvent(event: MyReservationsEvent) {
        when (event) {
            is ReservationClick -> _selectedReservation.update { event.reservation }
            is DateRangeSelect -> {
                observeMyReservations(
                    event.startDate,
                    event.endDate,
                )
                selectedStartDate = event.startDate
                selectedEndDate = event.endDate
            }

            is ReservationUpdated -> screenModelScope.launch {
                _viewEffect.send(
                    MyReservationsViewEffect.ReservationUpdated,
                )
            }

            is RetryClick -> observeMyReservations(selectedStartDate, selectedEndDate)
        }
    }

    private fun observeMyReservations(
        startDate: Long,
        endDate: Long,
    ) {
        observeJob?.cancel()
        observeJob = screenModelScope.launch {
            _uiState.update { MyReservationsUiState.Loading }
            observeMyParkingRequests(startDate, endDate).collect { reservations ->
                _uiState.update { screenMapper.map(reservations) }
            }
        }
    }
}
