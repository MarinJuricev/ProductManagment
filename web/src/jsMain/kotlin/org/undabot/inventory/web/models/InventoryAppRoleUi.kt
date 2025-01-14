package org.product.inventory.web.models

import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import user.model.InventoryAppRole

sealed class InventoryAppRoleUi(
    open val text: String,
    val authLevel: Int,
) {

    data class User(
        override val text: String,
    ) : InventoryAppRoleUi(text = text, authLevel = 1)

    data class Manager(
        override val text: String,
    ) : InventoryAppRoleUi(text = text, authLevel = 2)

    data class Administrator(
        override val text: String,
    ) : InventoryAppRoleUi(text = text, authLevel = 3)
}

fun InventoryAppRoleUi.toInventoryAppRole(): InventoryAppRole = when (this) {
    is InventoryAppRoleUi.User -> InventoryAppRole.User
    is InventoryAppRoleUi.Manager -> InventoryAppRole.Manager
    is InventoryAppRoleUi.Administrator -> InventoryAppRole.Administrator
}

fun InventoryAppRole.toInventoryAppRoleUi(
    dictionary: Dictionary,
): InventoryAppRoleUi = when (this) {
    InventoryAppRole.Administrator -> InventoryAppRoleUi.Administrator(dictionary.get(StringRes.userRoleAdministrator))
    InventoryAppRole.Manager -> InventoryAppRoleUi.Manager(dictionary.get(StringRes.userRoleManager))
    InventoryAppRole.User -> InventoryAppRoleUi.User(dictionary.get(StringRes.userRoleUser))
}
