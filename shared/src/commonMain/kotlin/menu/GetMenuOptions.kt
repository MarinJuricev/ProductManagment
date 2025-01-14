package menu

import auth.Authentication
import menu.model.MenuOption
import user.model.InventoryAppUser

class GetMenuOptions(
    val authentication: Authentication,
) {
    operator fun invoke(user: InventoryAppUser): List<MenuOption> =
        MenuOption.entries.filter { it.requiredRole <= user.role }
}
