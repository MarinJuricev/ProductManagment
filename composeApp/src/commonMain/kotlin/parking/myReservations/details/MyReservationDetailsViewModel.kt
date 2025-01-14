package parking.myReservations.details

import arrow.core.Either.Left
import arrow.core.Either.Right
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.myReservations.details.interaction.MyReservationDetailsEvent
import parking.myReservations.details.interaction.MyReservationDetailsEvent.CancelReservationButtonClick
import parking.myReservations.details.interaction.MyReservationDetailsEvent.CancelReservationConfirmed
import parking.myReservations.details.interaction.MyReservationDetailsEvent.CancelReservationDismissed
import parking.myReservations.details.interaction.MyReservationDetailsEvent.ScreenShown
import parking.myReservations.details.interaction.MyReservationDetailsUiState
import parking.myReservations.details.interaction.MyReservationDetailsUiState.Content
import parking.myReservations.details.interaction.MyReservationDetailsUiState.Loading
import parking.myReservations.details.interaction.MyReservationDetailsViewEffect
import parking.myReservations.details.interaction.MyReservationDetailsViewEffect.ReservationUpdated
import parking.myReservations.details.interaction.MyReservationDetailsViewEffect.ShowMessage
import parking.myReservations.details.mapper.CancelParkingReservationQuestionDialogMapper
import parking.myReservations.details.mapper.MyReservationDetailsUiMapper
import parking.myReservations.details.mapper.ParkingReservationErrorMapper
import parking.myReservations.details.mapper.ParkingReservationMapper
import parking.reservation.CancelParkingPlaceRequestByUser

class MyReservationDetailsViewModel(
    private val uiMapper: MyReservationDetailsUiMapper,
    private val cancelParkingRequest: CancelParkingPlaceRequestByUser,
    private val errorMapper: ParkingReservationErrorMapper,
    private val reservationMapper: ParkingReservationMapper,
    private val cancelReservationQuestionMapper: CancelParkingReservationQuestionDialogMapper,
) : ScreenModel {

    private val _uiState = MutableStateFlow<MyReservationDetailsUiState>(Loading)
    val uiState = _uiState.asStateFlow()

    private val _viewEffect = Channel<MyReservationDetailsViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<MyReservationDetailsViewEffect> = _viewEffect.receiveAsFlow()

    fun onEvent(event: MyReservationDetailsEvent) {
        when (event) {
            is CancelReservationButtonClick -> showQuestionDialog(event.data)
            is ScreenShown -> _uiState.update { uiMapper.map(event.selectedRequest) }
            is CancelReservationConfirmed -> cancelReservation(event.data)
            is CancelReservationDismissed -> dismissQuestionDialog()
        }
    }

    private fun showQuestionDialog(data: Content) {
        _uiState.updateContent {
            it.copy(
                cancelRequestDialog = cancelReservationQuestionMapper.map(
                    date = data.dateInfo.date,
                ),
            )
        }
    }

    private fun dismissQuestionDialog() {
        _uiState.updateContent { it.copy(cancelRequestDialog = null) }
    }

    private fun cancelReservation(data: Content) = screenModelScope.launch {
        _uiState.updateContent {
            it.copy(
                cancelButton = it.cancelButton.copy(cancelButtonLoading = true),
                cancelRequestDialog = null,
            )
        }
        when (
            val result = cancelParkingRequest(
                parkingReservation = reservationMapper.map(data),
            )
        ) {
            is Left -> _viewEffect.send(ShowMessage(errorMapper.map(result.value)))
            is Right -> _viewEffect.send(ReservationUpdated)
        }
        _uiState.updateContent {
            it.copy(cancelButton = it.cancelButton.copy(cancelButtonLoading = true))
        }
    }

    private fun MutableStateFlow<MyReservationDetailsUiState>.updateContent(
        update: (Content) -> Content,
    ) {
        this.update {
            when (it) {
                is Content -> update(it)
                else -> it
            }
        }
    }
}
