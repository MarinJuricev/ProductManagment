package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.models.ParkingSelectionData
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

sealed interface UserRequestDetailsState {

    data object Loading : UserRequestDetailsState

    data class UserRequestDetailsItemUi(
        val id: String,
        val statusDropdownEntries: List<ParkingReservationStatusUi>,
        val approveNoteLabel: String,
        val approveNote: String,
        val saveButtonText: String,
        val adminNotesLabel: String,
        val rejectReasonLabel: String,
        val rejectReason: String,
        val savingActive: Boolean,
        val saveVisible: Boolean,
        val garageLevelAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
        val requestedDateLabel: String,
        val requestedDateDateInput: DateInputValue,
        val submittedDateLabel: String,
        val submittedDate: String,
        val status: ParkingReservationStatusUi,
        val note: String,
        val parkingSelectionData: ParkingSelectionData,
        val hasPermanentGarageAccessLabel: String,
        val hasPermanentGarageAccess: Boolean,
        val garageAccessSwitchVisible: Boolean,
    ) : UserRequestDetailsState
}
