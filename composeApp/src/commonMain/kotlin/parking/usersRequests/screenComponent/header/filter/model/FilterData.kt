package parking.usersRequests.screenComponent.header.filter.model

import core.utils.endOfTheDay
import core.utils.startOfTheDay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import parking.reservation.model.ParkingReservationStatusUiModel
import utils.getMillisOfLastDayInCurrentMonth

data class FilterData(
    val dateRange: DateRange = DateRange(),
    val email: String = "",
    val statusUiModel: ParkingReservationStatusUiModel? = null,
)

data class DateRange(
    val startDate: Long = Clock.System.now().toEpochMilliseconds().startOfTheDay(UTC).toLong(),
    val endDate: Long = getMillisOfLastDayInCurrentMonth().endOfTheDay().toLong(),
)
