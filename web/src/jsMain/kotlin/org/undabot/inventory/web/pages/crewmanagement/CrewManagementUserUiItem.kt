package org.product.inventory.web.pages.crewmanagement

import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.models.InventoryAppRoleUi

data class CrewManagementUserUiItem(
    val id: String,
    val email: String,
    val profileInfo: ProfileInfo,
    val roleUi: InventoryAppRoleUi,
    val hasPermanentGarageAccess: Boolean,
)
