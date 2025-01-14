package org.product.inventory.web.pages.newrequest

import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import parking.reservation.PARKING_REQUESTS_LIMIT
import parking.reservation.model.MultipleParkingRequestState
import user.model.InventoryAppRole
import user.model.InventoryAppUser

class MultiDateSelectionStateMapper(
    private val dictionary: Dictionary,
) {

    private val stateStaticData by lazy {
        MultiDateSelectionStaticData(
            duplicateParkingReservationErrorMessage = dictionary.get(StringRes.parkingReservationDuplicateRequestErrorMessage),
            addDateButtonText = dictionary.get(StringRes.parkingReservationNewRequestAddDate),
        )
    }

    fun buildMultipleSelectionState(
        reservedDates: List<DateInputValue>,
        selectedRequestDates: Map<DateInputValue, MultipleParkingRequestState>,
        inventoryAppUser: InventoryAppUser,
    ) = if (inventoryAppUser.role == InventoryAppRole.User) {
        MultiDateSelectionState(
            staticData = stateStaticData,
            previouslyReservedDates = reservedDates,
            selectedRequestDates = selectedRequestDates.sortByDate(),
            shouldShowDateInput = selectedRequestDates.size != PARKING_REQUESTS_LIMIT,
            shouldShowAddDateButton = selectedRequestDates.size != PARKING_REQUESTS_LIMIT,
        )
    } else {
        MultiDateSelectionState()
    }

    private fun Map<DateInputValue, MultipleParkingRequestState>.sortByDate() =
        entries
            .sortedBy { it.key.value }
            .associate { it.toPair() }
}
