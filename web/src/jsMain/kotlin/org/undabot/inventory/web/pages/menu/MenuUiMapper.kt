package org.product.inventory.web.pages.menu

import org.product.inventory.web.components.toProfileInfo
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import user.model.InventoryAppUser

class MenuUiMapper(
    private val dictionary: Dictionary,
) {

    fun toUiState(
        currentUser: InventoryAppUser?,
        menuItems: List<MenuItem>,
        isVisible: Boolean,
    ) = when (currentUser) {
        null -> MenuState()
        else -> MenuState(
            profileInfo = currentUser.toProfileInfo(dictionary),
            menuItems = menuItems,
            isVisible = isVisible,
            logoutText = dictionary.get(StringRes.menuLogout),
        )
    }
}
