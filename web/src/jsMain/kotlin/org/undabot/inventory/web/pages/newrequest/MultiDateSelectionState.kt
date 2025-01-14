package org.product.inventory.web.pages.newrequest

import org.product.inventory.shared.MR
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.toCssColor
import parking.reservation.model.MultipleParkingRequestState

data class MultiDateSelectionState(
    val staticData: MultiDateSelectionStaticData = MultiDateSelectionStaticData(),
    val previouslyReservedDates: List<DateInputValue> = emptyList(),
    val selectedRequestDates: Map<DateInputValue, MultipleParkingRequestState> = emptyMap(),
    val shouldShowDateInput: Boolean = true,
    val shouldShowAddDateButton: Boolean = false,
)

fun MultipleParkingRequestState.getBackgroundColor() = when (this) {
    MultipleParkingRequestState.SUCCESS -> MR.colors.approvedStatus.toCssColor()
    MultipleParkingRequestState.FAILURE -> MR.colors.rejectedStatus.toCssColor()
    MultipleParkingRequestState.LOADING -> MR.colors.secondary.toCssColor()
}

data class MultiDateSelectionStaticData(
    val duplicateParkingReservationErrorMessage: String = "",
    val addDateButtonText: String = "",
)
