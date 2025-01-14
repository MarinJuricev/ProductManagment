package parking.crewManagement.interaction

import user.model.InventoryAppUser

sealed interface CrewManagementScreenState {
    data object Loading : CrewManagementScreenState
    data class Content(
        val users: List<InventoryAppUser>,
        val formTitle: String,
    ) : CrewManagementScreenState
}
