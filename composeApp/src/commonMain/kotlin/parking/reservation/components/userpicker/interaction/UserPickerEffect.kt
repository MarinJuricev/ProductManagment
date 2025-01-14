package parking.reservation.components.userpicker.interaction

import user.model.InventoryAppUser

sealed interface UserPickerEffect {
    data object ClosePicker : UserPickerEffect
    data class SelectUser(val user: InventoryAppUser) : UserPickerEffect
}
