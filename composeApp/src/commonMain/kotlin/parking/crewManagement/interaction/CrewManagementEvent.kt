package parking.crewManagement.interaction

import user.model.InventoryAppUser

sealed interface CrewManagementEvent {

    data class UserSelected(
        val user: InventoryAppUser,
    ) : CrewManagementEvent

    data object CreateUserClick : CrewManagementEvent
}
