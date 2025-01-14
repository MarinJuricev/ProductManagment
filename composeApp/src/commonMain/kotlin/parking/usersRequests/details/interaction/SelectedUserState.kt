package parking.usersRequests.details.interaction

import user.model.InventoryAppUser

sealed interface SelectedUserState {
    data class Received(val user: InventoryAppUser) : SelectedUserState
    data object Error : SelectedUserState
    data object Loading : SelectedUserState
}
