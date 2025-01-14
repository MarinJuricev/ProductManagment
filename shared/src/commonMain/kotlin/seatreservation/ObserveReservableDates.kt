package seatreservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import core.utils.dayFormatted
import core.utils.millisNow
import core.utils.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import seatreservation.model.ReservableDate
import seatreservation.model.SeatReservationError
import seatreservation.model.SeatReservationError.InvalidDates
import user.model.InventoryAppUser
import user.usecase.ObserveUsers

class ObserveReservableDates(
    private val observeSeatReservations: ObserveSeatReservations,
    private val observeUsers: ObserveUsers,
    private val observeOffice: ObserveOffice,
) {
    operator fun invoke(
        officeId: String,
        fromDate: Long = millisNow(),
        timezone: TimeZone = TimeZone.currentSystemDefault(),
    ): Either<SeatReservationError, Flow<List<ReservableDate>>> = either {
        val toDate = millisNow(dayOffset = 12)

        val emptyReservableDates = generateDates(
            fromDate = fromDate,
            toDate = toDate,
        ).associateBy { it.date.toLocalDate(timezone) }.toMutableMap()

        combine(
            observeUsers(),
            observeSeatReservations(
                officeId = officeId,
                fromDate = fromDate,
                toDate = toDate,
            ),
            observeOffice(id = officeId),
        ) { users, seatReservations, office ->
            val reservableDates = emptyReservableDates.toMutableMap()
            val groupedFilteredReservations = seatReservations
                .filter { reservation ->
                    reservation.officeId == officeId
                }
                .groupBy { it.date.toLocalDate(timezone) }

            for ((date, reservableDate) in reservableDates) {
                if (reservableDate is ReservableDate.Weekday) {
                    var seatHolders = emptyList<InventoryAppUser>()
                    groupedFilteredReservations[date]?.let { reservationsOnDate ->
                        seatHolders = reservationsOnDate.mapNotNull { reservation ->
                            users.firstOrNull { user -> user.id == reservation.userId }
                        }
                    }

                    val remainingSeats = office.numberOfSeats - seatHolders.size
                    reservableDates[date] = reservableDate.copy(
                        seatHolders = seatHolders,
                        remainingSeats = remainingSeats,
                    )
                }
            }

            reservableDates.values.toList()
        }
    }

    private fun Raise<InvalidDates>.generateDates(
        fromDate: Long,
        toDate: Long,
        timezone: TimeZone = TimeZone.currentSystemDefault(),
    ): List<ReservableDate> {
        ensure(fromDate <= toDate) { InvalidDates }

        val fromLocalDate = fromDate.toLocalDate(timezone)
        val toLocalDate = toDate.toLocalDate(timezone)

        // Generate the range of dates
        val dates = generateSequence(fromLocalDate) { date ->
            val nextDate = date.plus(1, DateTimeUnit.DAY)
            if (nextDate <= toLocalDate) nextDate else null
        }

        val emptyReservableDates = dates.map { date ->
            val millisecondsDate = date.atStartOfDayIn(timezone).toEpochMilliseconds()
            if (date.isWeekend()) {
                ReservableDate.Weekend(
                    day = date.dayFormatted(),
                    date = millisecondsDate,
                )
            } else {
                ReservableDate.Weekday(
                    day = date.dayFormatted(),
                    date = millisecondsDate,
                    remainingSeats = 0,
                    seatHolders = emptyList(),
                )
            }
        }

        return emptyReservableDates.toList()
    }

    private fun LocalDate.isWeekend(): Boolean = this.dayOfWeek == DayOfWeek.SATURDAY || this.dayOfWeek == DayOfWeek.SUNDAY
}
