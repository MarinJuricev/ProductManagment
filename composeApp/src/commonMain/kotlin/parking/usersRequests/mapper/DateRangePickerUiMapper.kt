package parking.usersRequests.mapper

import core.utils.endOfTheDay
import core.utils.startOfTheDay
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangeData
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerUiState
import utils.convertMillisToDate
import utils.getMillisOfLastDayInCurrentMonth

class DateRangePickerUiMapper(
    val dictionary: Dictionary,
) {
    fun map(
        startDate: Long = System.currentTimeMillis().startOfTheDay().toLong(),
        endDate: Long = getMillisOfLastDayInCurrentMonth().endOfTheDay().toLong(),
    ): DateRangePickerUiState = DateRangePickerUiState(
        startDatePickerTitle = dictionary.getString(MR.strings.parking_reservation_item_from),
        endDatePickerTitle = dictionary.getString(MR.strings.parking_reservation_item_to),
        dateRangeData = DateRangeData(
            displayedStartDate = convertMillisToDate(startDate),
            displayedEndDate = convertMillisToDate(endDate),
            selectedStartDateTimestamp = startDate,
            selectedEndDateTimestamp = endDate,
            loverDateLimit = startDate,
            upperDateLimit = endDate,
        ),
        startDateError = null,
        endDateError = null,
        confirmButtonText = dictionary.getString(MR.strings.general_ok),
    )
}
