package org.product.inventory.web.pages.seatreservation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.core.Page
import org.product.inventory.web.core.rememberNavigator
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.menu.MenuEvent
import org.product.inventory.web.pages.menu.MenuItemType
import org.product.inventory.web.pages.menu.MenuViewModel

@Page(Routes.seatReservation)
@Composable
fun SeatReservationPage() {
    val menuViewModel = rememberViewModel<MenuViewModel>()
    menuViewModel.onEvent(MenuEvent.UpdateSelectedMenuItem(MenuItemType.SeatReservation))

    val scope = rememberCoroutineScope()
    val viewModel = rememberViewModel<SeatReservationViewModel>(scope)
    val navigator = rememberNavigator()
    val state by viewModel.state.collectAsState()

    state.routeToNavigate?.let(navigator::navigateToRoute)

    SeatReservation(
        state = state,
        onEvent = viewModel::onEvent,
    )
}
