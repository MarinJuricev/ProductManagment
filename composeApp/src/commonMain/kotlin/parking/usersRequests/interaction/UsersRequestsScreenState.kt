package parking.usersRequests.interaction

import core.utils.endOfTheDay
import core.utils.startOfTheDay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import parking.reservation.model.ParkingReservationUiModel
import utils.getMillisOfLastDayInCurrentMonth

sealed class UsersRequestsScreenState(
    val initialStartDate: Long = Clock.System.now().toEpochMilliseconds().startOfTheDay(UTC).toLong(),
    val initialEndDate: Long = getMillisOfLastDayInCurrentMonth().endOfTheDay().toLong(),
) {
    data class Content(
        val requests: List<ParkingReservationUiModel> = listOf(),
    ) : UsersRequestsScreenState()

    data object Loading : UsersRequestsScreenState()

    data class EmptyState(
        val emptyStateTitle: String,
        val emptyStateMessage: String,
    ) : UsersRequestsScreenState()

    data class Retry(
        val retryStateTitle: String,
        val retryStateMessage: String,
    ) : UsersRequestsScreenState()
}
