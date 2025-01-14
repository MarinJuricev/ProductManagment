package org.product.inventory.web.pages.newrequest

import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.ParkingOption

sealed interface NewRequestEvent {

    data object SubmitClick : NewRequestEvent

    data class PathClick(val path: String) : NewRequestEvent

    data class DateChanged(
        val date: DateInputValue,
    ) : NewRequestEvent

    data class NotesChanged(
        val notes: String,
    ) : NewRequestEvent

    data object ProfileInfoClick : NewRequestEvent

    data class UserListFilterChanged(val text: String) : NewRequestEvent

    data class UserListItemClick(val userListItem: UserListItem) : NewRequestEvent

    data object UserListClosed : NewRequestEvent

    data class GarageLevelChanged(val garageLevel: ParkingOption) : NewRequestEvent

    data class ParkingSpotChanged(val parkingSpot: ParkingOption) : NewRequestEvent

    data object ReFetchGarageLevelDataClick : NewRequestEvent

    data object ToggleGarageAccess : NewRequestEvent

    data class AddRequestDate(val date: DateInputValue) : NewRequestEvent

    data class RemoveRequestDate(val date: DateInputValue) : NewRequestEvent
}
