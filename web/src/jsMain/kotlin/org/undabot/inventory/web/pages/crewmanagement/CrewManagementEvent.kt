package org.product.inventory.web.pages.crewmanagement

import org.product.inventory.web.models.InventoryAppRoleUi

sealed interface CrewManagementEvent {

    data class PathClick(val path: String) : CrewManagementEvent

    data class UserClick(val userId: String) : CrewManagementEvent

    data object AddUserClick : CrewManagementEvent

    data class EmailChanged(val email: String) : CrewManagementEvent

    data class RoleChanged(val role: InventoryAppRoleUi) : CrewManagementEvent

    data class HasPermanentGarageAccessChanged(val hasPermanentGarageAccess: Boolean) : CrewManagementEvent

    data object SaveClick : CrewManagementEvent

    data object DetailsDialogClosed : CrewManagementEvent
}
