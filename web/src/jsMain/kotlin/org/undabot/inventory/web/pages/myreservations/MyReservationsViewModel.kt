package org.product.inventory.web.pages.myreservations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.web.components.toLocalDate
import org.product.inventory.web.core.DateRange
import org.product.inventory.web.datetime.isBeforeOrEqual
import org.product.inventory.web.datetime.millis
import org.product.inventory.web.pages.Routes
import parking.dashboard.model.ParkingDashboardOption
import parking.reservation.CancelParkingPlaceRequestByUser
import parking.reservation.ObserveMyParkingRequests
import parking.role.ObserveScreenUnavailability
import user.usecase.ObserveCurrentUser

class MyReservationsViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: MyReservationsUiMapper,
    private val observeMyParkingRequests: ObserveMyParkingRequests,
    private val cancelParkingPlaceRequestByUser: CancelParkingPlaceRequestByUser,
    observeCurrentUser: ObserveCurrentUser,
    observeScreenUnavailability: ObserveScreenUnavailability,
) {

    private val _dateRange = MutableStateFlow(DateRange())
    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    private val _cancellationStatus = MutableStateFlow(CancellationStatus.Initial)
    private val _selectedReservation = MutableStateFlow<ParkingReservationItemUi?>(null)

    val selectedReservation = combine(
        _cancellationStatus,
        _selectedReservation,
    ) { status, reservation -> reservation?.copy(cancellationStatus = status) }
        .stateIn(
            scope = scope,
            started = SharingStarted.Companion.Lazily,
            initialValue = null,
        )

    private val myReservations = _dateRange
        .onEach { _isLoading.update { true } }
        .flatMapLatest { dateRange ->
            observeMyParkingRequests(
                startDate = dateRange.fromDate.millis,
                endDate = dateRange.toDate.millis,
            )
        }
        .onEach { _isLoading.update { false } }
        .stateIn(
            scope = scope,
            started = SharingStarted.Companion.Lazily,
            initialValue = emptyList(),
        )

    val state = combine(
        myReservations,
        _dateRange,
        observeCurrentUser(),
        _isLoading,
        _routeToNavigate,
        uiMapper::buildUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Companion.Lazily,
        initialValue = MyReservationsState(),
    )

    init {
        observeScreenUnavailability(ParkingDashboardOption.MyReservationsOption.requiredRole)
            .onEach { _routeToNavigate.update { Routes.parkingReservation } }
            .launchIn(scope)
    }

    fun onEvent(event: MyReservationsEvent) {
        when (event) {
            is MyReservationsEvent.DateRangeChanged -> updateDateRange(event)
            is MyReservationsEvent.ItemClick -> _selectedReservation.update {
                state.value.reservations.firstOrNull { it.itemId == event.itemId }
            }
            MyReservationsEvent.CancelReservationClick -> _cancellationStatus.update { CancellationStatus.Confirmation }
            is MyReservationsEvent.PathClick -> _routeToNavigate.update { event.path }
            MyReservationsEvent.CancelCanceledClick -> _cancellationStatus.update { CancellationStatus.Initial }
            is MyReservationsEvent.CancelConfirmClick -> scope.launch { handleCancelConfirmClick(event.itemId) }
            MyReservationsEvent.CloseDialogClick -> _cancellationStatus.update { CancellationStatus.Initial }
            MyReservationsEvent.DetailsClosed -> clearDetailsData()
        }
    }

    private fun updateDateRange(event: MyReservationsEvent.DateRangeChanged) {
        _dateRange.update {
            val fromDate = event.fromDate?.toLocalDate() ?: it.fromDate
            val toDate = event.toDate?.toLocalDate() ?: it.toDate

            DateRange(
                fromDate = fromDate,
                toDate = if (toDate.isBeforeOrEqual(fromDate)) fromDate else toDate,
            )
        }
    }

    private suspend fun handleCancelConfirmClick(itemId: String) {
        val item = myReservations.value
            .firstOrNull { it.id == itemId }
            ?: return
        cancelParkingPlaceRequestByUser(parkingReservation = item)
        _cancellationStatus.update { CancellationStatus.Initial }
    }

    private fun clearDetailsData() {
        _selectedReservation.update { null }
        _cancellationStatus.update { CancellationStatus.Initial }
    }
}
