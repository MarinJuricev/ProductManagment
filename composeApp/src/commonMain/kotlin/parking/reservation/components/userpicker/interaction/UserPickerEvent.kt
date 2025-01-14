package parking.reservation.components.userpicker.interaction

import user.model.InventoryAppUser

sealed interface UserPickerEvent {

    data class UserPreselected(val user: InventoryAppUser) : UserPickerEvent
    data class SearchValueChange(val search: String) : UserPickerEvent
    data class UserClick(val user: InventoryAppUser) : UserPickerEvent
    data class SaveClick(val user: InventoryAppUser) : UserPickerEvent
    data object CancelClick : UserPickerEvent
}
