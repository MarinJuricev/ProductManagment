package parking.usersRequests

import UsersRequestsScreenMapper
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.reservation.model.ParkingReservationUiModel
import parking.usersRequests.interaction.UsersRequestsEvent
import parking.usersRequests.interaction.UsersRequestsEvent.DateRangeSelect
import parking.usersRequests.interaction.UsersRequestsEvent.EmailFilterApplied
import parking.usersRequests.interaction.UsersRequestsEvent.ParkingReservationUpdate
import parking.usersRequests.interaction.UsersRequestsEvent.ReservationClick
import parking.usersRequests.interaction.UsersRequestsEvent.StatusFilterApplied
import parking.usersRequests.interaction.UsersRequestsScreenState.Loading
import parking.usersRequests.interaction.UsersRequestsViewEffect
import parking.usersRequests.interaction.UsersRequestsViewEffect.CloseDetails
import parking.usersRequests.screenComponent.header.filter.model.DateRange
import parking.usersRequests.screenComponent.header.filter.model.FilterData

class UsersRequestsScreenViewModel(
    private val screenMapper: UsersRequestsScreenMapper,
    private val observeUserRequests: ObserveUserRequests,
) : ScreenModel {

    private val filterData = MutableStateFlow(FilterData())

    private val _selectedReservation = MutableStateFlow<ParkingReservationUiModel?>(null)
    val selectedReservation = _selectedReservation.asStateFlow()

    private val _viewEffect = Channel<UsersRequestsViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<UsersRequestsViewEffect> = _viewEffect.receiveAsFlow()

    private val userRequests = filterData
        .map { it.dateRange }
        .distinctUntilChanged()
        .flatMapLatest {
            observeUserRequests(startDate = it.startDate, endDate = it.endDate)
        }

    val state = combine(userRequests, filterData, screenMapper::map).stateIn(
        scope = screenModelScope,
        started = SharingStarted.Lazily,
        initialValue = Loading,
    )

    fun onEvent(event: UsersRequestsEvent) {
        when (event) {
            is ReservationClick -> _selectedReservation.update { event.reservation }
            is DateRangeSelect -> filterData.update {
                it.copy(dateRange = DateRange(startDate = event.startDate, endDate = event.endDate))
            }

            is ParkingReservationUpdate -> screenModelScope.launch { _viewEffect.send(CloseDetails) }
            is EmailFilterApplied -> filterData.update { it.copy(email = event.email) }
            is StatusFilterApplied -> filterData.update { it.copy(statusUiModel = event.status) }
        }
    }
}
