import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.mapper.ParkingReservationUiModelMapper
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.reservation.model.ParkingReservationStatus.Canceled
import parking.reservation.model.ParkingReservationStatus.Declined
import parking.reservation.model.ParkingReservationStatus.Submitted
import parking.usersRequests.interaction.UsersRequestsScreenState
import parking.usersRequests.interaction.UsersRequestsScreenState.Content
import parking.usersRequests.screenComponent.header.filter.model.FilterData

class UsersRequestsScreenMapper(
    dictionary: Dictionary,
    private val parkingReservationUiMapper: ParkingReservationUiModelMapper,
) {
    fun map(
        usersRequests: List<ParkingReservation>,
        filterData: FilterData,
    ) = if (usersRequests.isEmpty()) {
        emptyState
    } else {
        val filteredRequests = usersRequests
            .filter { item ->
                item.email.contains(
                    other = filterData.email,
                    ignoreCase = true,
                ) && when (val appliedFilterStatus = filterData.statusUiModel?.status) {
                    null -> true
                    else -> item.status.matchesType(appliedFilterStatus)
                }
            }.map(parkingReservationUiMapper::invoke)

        if (filteredRequests.isEmpty()) {
            emptyState
        } else {
            Content(requests = filteredRequests)
        }
    }

    private val emptyState = UsersRequestsScreenState.EmptyState(
        emptyStateTitle = dictionary.getString(MR.strings.parking_reservation_empty_state_error_title),
        emptyStateMessage = dictionary.getString(MR.strings.parking_reservation_empty_state_error_message),
    )

    private fun ParkingReservationStatus.matchesType(other: ParkingReservationStatus): Boolean =
        when (this) {
            is Submitted -> other is Submitted
            is Approved -> other is Approved
            is Declined -> other is Declined
            is Canceled -> other is Canceled
        }
}
