package org.product.inventory.web.pages.crewmanagement

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.models.InventoryAppRoleUi

data class CrewManagementUiState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val isLoading: Boolean = true,
    val registeredUsersLabel: String = "",
    val routeToNavigate: String? = null,
    val alertMessage: AlertMessage? = null,
    val users: List<CrewManagementUserUiItem> = emptyList(),
    val addButtonText: String = "",
    val closeDetailsDialog: Boolean = false,
)

data class CrewManagementUserDetailsState(
    val userId: String?,
    val profileIcon: String?,
    val email: String,
    val role: InventoryAppRoleUi,
    val hasPermanentGarageAccess: Boolean,
    val isUpdating: Boolean,
    val availableRoles: List<InventoryAppRoleUi>,
    val emailLabel: String,
    val roleLabel: String,
    val hasPermanentGarageAccessLabel: String,
    val saveButtonText: String,
    val saveEnabled: Boolean,
    val emailErrorMessage: String?,
    val emailEditable: Boolean,
)

sealed interface UserUpsertMode {
    data object New : UserUpsertMode
    data class Edit(val userId: String) : UserUpsertMode
}
