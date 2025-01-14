package seat.management.interaction

import seat.management.components.OfficeItemOption
import seatreservation.model.Office

sealed interface SeatManagementScreenEvent {
    data class OfficeNameChange(val name: String) : SeatManagementScreenEvent
    data class AddOfficeClick(val name: String, val seats: String) : SeatManagementScreenEvent
    data class SeatsChanged(val seats: String) : SeatManagementScreenEvent
    data object DeleteOfficeConfirmed : SeatManagementScreenEvent
    data object DeleteOfficeCanceled : SeatManagementScreenEvent
    data class OnOfficeOptionClick(
        val option: OfficeItemOption,
        val office: Office,
    ) : SeatManagementScreenEvent

    data class EditOfficeNameChanged(
        val office: Office,
        val name: String,
    ) : SeatManagementScreenEvent

    data class EditOfficeSeatsChanged(
        val office: Office,
        val seats: String,
    ) : SeatManagementScreenEvent
    data class EditOfficeSaveClick(val office: Office) : SeatManagementScreenEvent
    data object EditOfficeCancelClick : SeatManagementScreenEvent
}
