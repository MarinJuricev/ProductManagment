package parking.crewManagement.interaction

import user.model.InventoryAppUser

sealed interface CrewManagementViewEffect {
    data class ShowMessage(val message: String) : CrewManagementViewEffect
    data class ShowUserDetails(val user: InventoryAppUser) : CrewManagementViewEffect
    data object CreateNewUser : CrewManagementViewEffect
}
