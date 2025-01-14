package parking.reservation.mapper

import core.utils.endOfTheDay
import core.utils.millisNow
import core.utils.startOfTheDay
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.DateWithTimestamp
import parking.reservation.model.MultiDateSelectionState
import parking.reservation.model.MultiDateSelectionStaticData
import parking.reservation.model.MultipleParkingRequestState
import parking.reservation.model.MultipleParkingRequestState.LOADING
import parking.reservation.model.ParkingReservation
import utils.convertMillisToDate

class MultiDateSelectionUiMapper(
    dictionary: Dictionary,
) {
    fun map(
        usersReservations: List<ParkingReservation>,
        selectedDates: List<Long>,
        requestsForSubmission: Map<Long, MultipleParkingRequestState>,
    ) = MultiDateSelectionState(
        selectedDates = selectedDates.map { DateWithTimestamp(it, convertMillisToDate(it)) },
        forbiddenDates = selectedDates + usersReservations.map { it.date },
        staticData = staticData,
        addingNewDateEnabled = selectedDates.size < MAX_DATES,
        lowerDateLimit = millisNow(dayOffset = 1).startOfTheDay().toLong(),
        upperDateLimit = millisNow(dayOffset = 14).endOfTheDay().toLong(),
        preselectedDate = millisNow(dayOffset = 1).startOfTheDay().toLong(),
        requestsForSubmission = requestsForSubmission.mapKeys { convertMillisToDate(it.key) },
        submitStatusSheetVisible = requestsForSubmission.isNotEmpty(),
        closeSubmitStatusSheetButtonEnabled = requestsForSubmission.values.all { it != LOADING },
    )

    private val staticData = MultiDateSelectionStaticData(
        formTitle = dictionary.getString(MR.strings.parking_reservation_dates),
        addDateButtonText = dictionary.getString(MR.strings.parking_reservation_add_new_date),
        datePickerConfirmSelectionText = dictionary.getString(MR.strings.general_ok),
        dateFormTitle = dictionary.getString(MR.strings.parking_reservation_new_request_date),
        statusFormTitle = dictionary.getString(MR.strings.parking_reservation_status),
        failedRequestError = dictionary.getString(MR.strings.parking_reservation_new_request_failure),
        closeButtonText = dictionary.getString(MR.strings.general_done),
    )
}

private const val MAX_DATES = 5
