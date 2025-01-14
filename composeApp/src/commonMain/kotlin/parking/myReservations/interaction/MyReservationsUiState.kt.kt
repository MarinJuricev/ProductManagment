package parking.myReservations.interaction

import parking.reservation.model.ParkingReservationUiModel

sealed interface MyReservationsUiState {
    data class Content(
        val requests: List<ParkingReservationUiModel> = listOf(),
    ) : MyReservationsUiState

    data object Loading : MyReservationsUiState

    data class EmptyState(
        val emptyStateTitle: String,
        val emptyStateMessage: String,
    ) : MyReservationsUiState

    data class Retry(
        val retryStateTitle: String,
        val retryStateMessage: String,
    ) : MyReservationsUiState
}
