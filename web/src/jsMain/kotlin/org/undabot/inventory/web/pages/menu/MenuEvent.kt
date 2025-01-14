package org.product.inventory.web.pages.menu

sealed interface MenuEvent {

    data object Logout : MenuEvent

    data class Navigate(
        val menuItemType: MenuItemType,
    ) : MenuEvent

    data class RouteChanged(
        val route: String,
    ) : MenuEvent

    data class UpdateSelectedMenuItem(
        val menuItemType: MenuItemType,
    ) : MenuEvent
}
