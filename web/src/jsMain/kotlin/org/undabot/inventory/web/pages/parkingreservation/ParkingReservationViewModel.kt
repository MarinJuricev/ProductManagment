package org.product.inventory.web.pages.parkingreservation

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.dashboard.GetParkingDashboard
import user.usecase.ObserveCurrentUser

class ParkingReservationViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: ParkingReservationUiMapper,
    private val getParkingDashboard: GetParkingDashboard,
    observeCurrentUser: ObserveCurrentUser,
) {

    private val _isLoading = MutableStateFlow(false)
    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val _manualFetchTrigger = Channel<Unit>()
    private val fetchTrigger = merge(
        _manualFetchTrigger.receiveAsFlow(),
        observeCurrentUser().map { it.role }.distinctUntilChanged(),
    )

    val state = combine(
        _isLoading,
        _routeToNavigate,
        fetchTrigger
            .onEach { _isLoading.update { true } }
            .map { getParkingDashboard() }
            .onStart { emit(Either.Right(emptyList())) }
            .onEach { _isLoading.update { false } },
        uiMapper::buildUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Companion.Lazily,
        initialValue = ParkingReservationState(),
    )

    fun onEvent(event: ParkingReservationEvent) {
        when (event) {
            is ParkingReservationEvent.ReservationItemClick ->
                _routeToNavigate.update { event.type.route }

            ParkingReservationEvent.RetryClick -> scope.launch { _manualFetchTrigger.send(Unit) }
            is ParkingReservationEvent.PathClick -> _routeToNavigate.update { event.path }
        }
    }
}
