package org.product.inventory.web.pages.seatreservationtmanagement

sealed interface SeatReservationManagementEvent {

    data class PathClick(val path: String) : SeatReservationManagementEvent

    data class DeleteOfficeClick(val office: OfficeUI) : SeatReservationManagementEvent

    data object DeleteOfficeConfirmClick : SeatReservationManagementEvent

    data object DeleteOfficeCancelClick : SeatReservationManagementEvent

    data object DeleteDialogClosed : SeatReservationManagementEvent

    data class NewOfficeNameChange(val name: String) : SeatReservationManagementEvent

    data class NewOfficeNumberOfSeatsChange(val numberOfSeats: String) : SeatReservationManagementEvent

    data class AddOfficeClick(val name: String, val numberOfSeats: String) : SeatReservationManagementEvent

    data class EditOfficeClick(val office: OfficeUI) : SeatReservationManagementEvent

    data class EditOfficeConfirmClick(val data: OfficeDetailsData) : SeatReservationManagementEvent

    data class EditOfficeNameChange(val name: String) : SeatReservationManagementEvent

    data class EditOfficeNumberOfSeatsChange(val numberOfSeats: String) : SeatReservationManagementEvent

    data object EditDialogClosed : SeatReservationManagementEvent
}
