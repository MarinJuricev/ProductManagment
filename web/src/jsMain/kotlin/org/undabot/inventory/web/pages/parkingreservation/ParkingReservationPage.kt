package org.product.inventory.web.pages.parkingreservation

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

@Page(Routes.parkingReservation)
@Composable
fun ParkingReservationPage() {
    val menuViewModel = rememberViewModel<MenuViewModel>()
    menuViewModel.onEvent(MenuEvent.UpdateSelectedMenuItem(MenuItemType.ParkingReservations))

    val scope = rememberCoroutineScope()
    val parkingReservationViewModel = rememberViewModel<ParkingReservationViewModel>(scope)
    val navigator = rememberNavigator()
    val reservationState by parkingReservationViewModel.state.collectAsState()

    reservationState.routeToNavigate?.let(navigator::navigateToRoute)

    ParkingReservation(
        state = reservationState,
        onEvent = parkingReservationViewModel::onEvent,
    )
}
