package org.product.inventory.web.pages.seatreservationtimeline

import core.utils.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.clearAfterDelay
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import seatreservation.CancelSeatReservation
import seatreservation.ObserveOffices
import seatreservation.ObserveReservableDates
import seatreservation.ReserveSeat
import seatreservation.model.Office
import user.usecase.ObserveCurrentUser

class SeatReservationTimelineViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: SeatReservationTimelineUiStateMapper,
    private val observeReservableDates: ObserveReservableDates,
    private val cancelSeatReservation: CancelSeatReservation,
    private val reserveSeat: ReserveSeat,
    private val alertMessageMapper: AlertMessageMapper,
    private val dictionary: Dictionary,
    observeOffices: ObserveOffices,
    observeCurrentUser: ObserveCurrentUser,
) {

    private val _isLoading = MutableStateFlow(true)
    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)
    private val _selectedOffice = MutableStateFlow<Office?>(null)
    private val _datesCtaStatus = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    private val _reservableDates = _selectedOffice
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { observeReservableDates(it.id).getOrNull() ?: flowOf(emptyList()) }
        // forced delay because of the UI flickering. observeReservableDates (UserRepository.observeUsers) emits incorrect data on first emission (probably cached data)
        // 300ms added after testing how much time is needed for the data to be correct
        .debounce(300L)
        .onEach { reservableDates -> _datesCtaStatus.update { reservableDates.associate { it.date to false } } }

    val state = combine(
        _isLoading,
        _alertMessage,
        _selectedOffice.onEach { _isLoading.update { true } },
        observeOffices().onEach(::updateSelectedOffice),
        _reservableDates.onEach { _isLoading.update { false } },
        observeCurrentUser(),
        uiMapper::toUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = SeatReservationTimelineUiState(),
    )

    val datesCtaStatus = _datesCtaStatus.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = emptyMap(),
    )

    fun onEvent(event: SeatReservationTimelineEvent) {
        when (event) {
            is SeatReservationTimelineEvent.OnOfficeChanged -> _selectedOffice.update { event.office }
            is SeatReservationTimelineEvent.OnActionOptionClick -> handleOnActionOptionClick(
                date = event.date,
                option = event.option,
                officeId = event.officeId,
            )
        }
    }

    private fun handleOnActionOptionClick(
        date: Long,
        option: ReservableActionOption,
        officeId: String,
    ) {
        scope.launch {
            updateDateCtaStatus(date = date, isLoading = true)
            when (option) {
                ReservableActionOption.RESERVE -> reserveSeat(officeId, date)
                ReservableActionOption.CANCEL -> cancelSeatReservation(officeId, date)
            }.onLeft {
                showAlertMessageWhenActionFails(option)
            }.also {
                updateDateCtaStatus(date = date, isLoading = false)
            }
        }
    }

    private fun updateDateCtaStatus(
        date: Long,
        isLoading: Boolean,
    ) {
        _datesCtaStatus.update {
            it.toMutableMap().apply { this[date] = isLoading }
        }
    }

    private fun updateSelectedOffice(
        offices: List<Office>,
    ) {
        _selectedOffice.update { selectedOffice ->
            if (offices.isEmpty()) null else selectedOffice ?: offices.firstOrNull()
        }
    }

    private fun showAlertMessageWhenActionFails(option: ReservableActionOption) {
        val actionFailureText = when (option) {
            ReservableActionOption.RESERVE -> StringRes.seatReservationTimelineReserveSeatFailureText
            ReservableActionOption.CANCEL -> StringRes.seatReservationTimelineCancelSeatFailureText
        }
        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = false,
                failureMessage = dictionary.get(actionFailureText),
            )
        }
        // Launches a coroutine to clear the alert message after a delay, without blocking other operations
        scope.launch { _alertMessage.clearAfterDelay() }
    }
}
