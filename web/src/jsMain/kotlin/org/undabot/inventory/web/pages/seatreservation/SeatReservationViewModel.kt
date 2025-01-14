package org.product.inventory.web.pages.seatreservation

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.product.inventory.web.pages.Routes
import seatreservation.dashboard.GetSeatReservationDashboardType
import seatreservation.dashboard.SeatReservationDashboardType
import seatreservation.dashboard.model.SearReservationDashboardError
import seatreservation.dashboard.model.SeatReservationOption

class SeatReservationViewModel(
    private val getSeatReservationDashboardType: GetSeatReservationDashboardType,
    scope: CoroutineScope,
    uiMapper: SeatReservationUiMapper,
) {

    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val _dashboardTypeFetchTrigger = Channel<Unit>()
    private val dashboardType = _dashboardTypeFetchTrigger.receiveAsFlow()
        .onStart { emit(Unit) }
        .map { getSeatReservationDashboardType() }

    val state = combine(
        _routeToNavigate,
        dashboardType.onEach(::handleUserRole),
        uiMapper::toUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = SeatReservationUiState(),
    )

    private fun handleUserRole(
        dashboardTypeOrError: Either<SearReservationDashboardError, SeatReservationDashboardType>,
    ) {
        dashboardTypeOrError.getOrNull()
            ?.let { it as? SeatReservationDashboardType.User }
            ?.let { _routeToNavigate.update { Routes.seatReservationTimeline } }
    }

    fun onEvent(event: SeatReservationEvent) {
        when (event) {
            is SeatReservationEvent.PathClick -> _routeToNavigate.update { event.path }
            is SeatReservationEvent.ItemClick -> handleDashboardItemClick(event.type)
        }
    }

    private fun handleDashboardItemClick(type: SeatReservationOption) {
        _routeToNavigate.update {
            when (type) {
                SeatReservationOption.SeatManagement -> Routes.seatReservationManagement
                SeatReservationOption.Timeline -> Routes.seatReservationTimeline
            }
        }
    }
}
