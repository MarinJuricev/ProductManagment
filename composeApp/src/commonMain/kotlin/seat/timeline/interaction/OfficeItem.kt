package seat.timeline.interaction

import seatreservation.model.Office

sealed interface OfficeItem {
    data object Undefined : OfficeItem
    data object Loading : OfficeItem
    data class OfficeData(val office: Office) : OfficeItem
}
