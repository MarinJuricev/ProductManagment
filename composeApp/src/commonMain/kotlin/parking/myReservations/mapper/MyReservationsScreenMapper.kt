import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.myReservations.interaction.MyReservationsUiState
import parking.reservation.mapper.ParkingReservationUiModelMapper
import parking.reservation.model.ParkingReservation

class MyReservationsScreenMapper(
    val dictionary: Dictionary,
    val parkingReservationUiMapper: ParkingReservationUiModelMapper,
) {
    fun map(
        usersRequests: List<ParkingReservation>,
    ) = if (usersRequests.isEmpty()) {
        MyReservationsUiState.EmptyState(
            emptyStateTitle = dictionary.getString(MR.strings.parking_reservation_empty_state_error_title),
            emptyStateMessage = dictionary.getString(MR.strings.parking_reservation_empty_state_error_message),
        )
    } else {
        MyReservationsUiState.Content(
            requests = usersRequests.map(parkingReservationUiMapper::invoke),
        )
    }
}
