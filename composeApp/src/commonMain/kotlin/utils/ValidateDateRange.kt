package utils

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class ValidateDateRange(
    private val dictionary: Dictionary,
) {
    operator fun invoke(
        startDate: Long,
        endDate: Long,
    ): Either<DateRangeError, Unit> = either {
        ensure(startDate <= endDate) {
            DateRangeError(
                startDateError = dictionary.getString(MR.strings.parking_reservation_date_range_error_start_date),
                endDateError = dictionary.getString(MR.strings.parking_reservation_date_range_error_end_date),
            )
        }
    }
}

data class DateRangeError(
    val startDateError: String,
    val endDateError: String,
)
