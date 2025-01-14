package org.product.inventory.web.pages.menu

sealed interface MenuViewEffect {
    data class OnNavigate(val route: String) : MenuViewEffect
}
