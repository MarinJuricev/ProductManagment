package seat.dashboard

import arrow.core.Either.Left
import arrow.core.Either.Right
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import seat.dashboard.interaction.SeatReservationDashboardScreenEvent
import seat.dashboard.interaction.SeatReservationDashboardScreenEvent.AdminDashboardOptionClick
import seat.dashboard.interaction.SeatReservationDashboardScreenEvent.RetryClick
import seat.dashboard.interaction.SeatReservationDashboardScreenState
import seat.dashboard.interaction.SeatReservationDashboardScreenState.Content
import seat.dashboard.interaction.SeatReservationDashboardScreenState.Loading
import seat.dashboard.interaction.SeatReservationDashboardScreenState.Retry
import seat.dashboard.interaction.SeatReservationDashboardViewEffect
import seat.dashboard.interaction.SeatReservationDashboardViewEffect.OpenDashboardOption
import seat.dashboard.interaction.SeatReservationDashboardViewEffect.OpenTimelineAsUser
import seat.management.SeatManagementScreen
import seat.timeline.SeatReservationTimelineScreen
import seatreservation.dashboard.GetSeatReservationDashboardType
import seatreservation.dashboard.SeatReservationDashboardType
import seatreservation.dashboard.SeatReservationDashboardType.Administrator
import seatreservation.dashboard.SeatReservationDashboardType.User
import seatreservation.dashboard.model.SeatReservationOption
import seatreservation.dashboard.model.SeatReservationOption.SeatManagement
import seatreservation.dashboard.model.SeatReservationOption.Timeline
import user.usecase.ObserveCurrentUser

class SeatReservationDashboardViewModel(
    private val getDashboardType: GetSeatReservationDashboardType,
    private val observeCurrentUser: ObserveCurrentUser,
) : ScreenModel {

    private val _uiState = MutableStateFlow<SeatReservationDashboardScreenState>(Loading)
    val uiState = _uiState.asStateFlow()
    private val _viewEffect = Channel<SeatReservationDashboardViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<SeatReservationDashboardViewEffect> = _viewEffect.receiveAsFlow()
    private var observeJob: Job? = null

    init {
        fetchDashboard()
    }

    fun onEvent(event: SeatReservationDashboardScreenEvent) {
        when (event) {
            is RetryClick -> fetchDashboard()
            is AdminDashboardOptionClick -> handleAdminDashboardOptionClick(event.option)
        }
    }

    private fun fetchDashboard() {
        _uiState.update { Loading }
        observeJob?.cancel()
        observeJob = screenModelScope.launch {
            observeCurrentUser().collect { user ->
                when (val result = getDashboardType(user)) {
                    is Left -> _uiState.update { Retry }
                    is Right -> result.value.handleDashboardType()
                }
            }
        }
    }

    private fun SeatReservationDashboardType.handleDashboardType() = screenModelScope.launch {
        when (this@handleDashboardType) {
            is Administrator -> _uiState.update { Content(options) }
            is User -> _viewEffect.send(OpenTimelineAsUser)
        }
    }

    private fun handleAdminDashboardOptionClick(option: SeatReservationOption) =
        screenModelScope.launch {
            when (option) {
                is SeatManagement -> _viewEffect.send(OpenDashboardOption(SeatManagementScreen()))
                is Timeline -> _viewEffect.send(OpenDashboardOption(SeatReservationTimelineScreen()))
            }
        }
}
