package parking.dashboard

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
import parking.dashboard.interaction.ParkingDashboardScreenEvent
import parking.dashboard.interaction.ParkingDashboardScreenEvent.DashboardOptionSelect
import parking.dashboard.interaction.ParkingDashboardScreenEvent.RetryButtonClick
import parking.dashboard.interaction.ParkingDashboardScreenState
import parking.dashboard.interaction.ParkingDashboardScreenState.Loading
import parking.dashboard.interaction.ParkingDashboardViewEffect
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToEmailTemplates
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToMyReservations
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToNewRequest
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToSlotsManagement
import parking.dashboard.interaction.ParkingDashboardViewEffect.NavigateToUserRequests
import parking.dashboard.mapper.ParkingDashboardScreenMapper
import parking.dashboard.model.ParkingDashboardOption.EmailTemplatesOption
import parking.dashboard.model.ParkingDashboardOption.MyReservationsOption
import parking.dashboard.model.ParkingDashboardOption.NewRequestOption
import parking.dashboard.model.ParkingDashboardOption.SlotsManagementOption
import parking.dashboard.model.ParkingDashboardOption.UserRequestsOption
import user.usecase.ObserveCurrentUser

class ParkingDashboardViewModel(
    private val getDashboard: GetParkingDashboard,
    private val screenMapper: ParkingDashboardScreenMapper,
    private val observeCurrentUser: ObserveCurrentUser,
) : ScreenModel {
    private val _uiState =
        MutableStateFlow<ParkingDashboardScreenState>(Loading)
    val uiState = _uiState.asStateFlow()
    private val _viewEffect = Channel<ParkingDashboardViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<ParkingDashboardViewEffect> = _viewEffect.receiveAsFlow()
    private var observeJob: Job? = null

    init {
        fetchDashboard()
    }

    fun onEvent(event: ParkingDashboardScreenEvent) {
        when (event) {
            is DashboardOptionSelect -> event.handle()
            is RetryButtonClick -> fetchDashboard()
        }
    }

    private fun DashboardOptionSelect.handle() = screenModelScope.launch {
        when (option) {
            EmailTemplatesOption -> _viewEffect.send(NavigateToEmailTemplates)
            MyReservationsOption -> _viewEffect.send(NavigateToMyReservations)
            NewRequestOption -> _viewEffect.send(NavigateToNewRequest)
            SlotsManagementOption -> _viewEffect.send(NavigateToSlotsManagement)
            UserRequestsOption -> _viewEffect.send(NavigateToUserRequests)
        }
    }

    private fun fetchDashboard() {
        _uiState.update { Loading }
        observeJob?.cancel()
        observeJob = screenModelScope.launch {
            observeCurrentUser().collect { user ->
                _uiState.update { screenMapper(getDashboard(user)) }
            }
        }
    }
}
