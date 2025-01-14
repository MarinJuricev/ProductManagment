package parking.reservation.components.userpicker.interaction

import user.model.InventoryAppUser

sealed interface UserPickerState {
    data object Loading : UserPickerState
    data class Content(
        val users: List<InventoryAppUser>,
        val searchValue: String,
        val searchPlaceholderText: String,
        val preselectedUser: InventoryAppUser,
    ) : UserPickerState
}
