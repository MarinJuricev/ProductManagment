package org.product.inventory.web.pages.menu

import auth.Authentication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import menu.GetMenuOptions
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.menu.MenuEvent.Logout
import org.product.inventory.web.pages.menu.MenuEvent.Navigate
import org.product.inventory.web.pages.menu.MenuEvent.RouteChanged
import org.product.inventory.web.pages.menu.MenuItemType.ParkingReservations
import org.product.inventory.web.pages.menu.MenuViewEffect.OnNavigate
import user.usecase.ObserveCurrentUser

class MenuViewModel(
    private val scope: CoroutineScope,
    private val menuUiMapper: MenuUiMapper,
    private val menuItemsUiMapper: MenuItemsUiMapper,
    private val authentication: Authentication,
    private val getMenuOptions: GetMenuOptions,
    observeCurrentUser: ObserveCurrentUser,
) {
    private val routesWithNoNavigation = setOf(Routes.auth)

    private val isVisible = MutableStateFlow(false)

    private val currentUser = observeCurrentUser().stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )

    private val _selectedMenuItemType = MutableStateFlow(ParkingReservations)
    private val menuItems = combine(
        _selectedMenuItemType,
        currentUser.filterNotNull().map { getMenuOptions(it) },
        menuItemsUiMapper::map,
    )

    val state = combine(
        currentUser,
        menuItems,
        isVisible,
        menuUiMapper::toUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Companion.Lazily,
        initialValue = MenuState(),
    )

    private val _viewEffect = Channel<MenuViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<MenuViewEffect> = _viewEffect.receiveAsFlow()

    fun onEvent(event: MenuEvent) {
        when (event) {
            Logout -> scope.launch {
                // since MenuViewModel is singleton we need to reset selected menu item to default
                _selectedMenuItemType.update { ParkingReservations }
                authentication.signOut()
            }
            is Navigate -> handleNavigate(event.menuItemType)
            is RouteChanged -> isVisible.update {
                event.route !in routesWithNoNavigation
            }
            is MenuEvent.UpdateSelectedMenuItem -> _selectedMenuItemType.update { event.menuItemType }
        }
    }

    private fun handleNavigate(menuItemType: MenuItemType) {
        scope.launch {
            _selectedMenuItemType.update { menuItemType }
            _viewEffect.send(OnNavigate(menuItemType.route))
        }
    }
}
