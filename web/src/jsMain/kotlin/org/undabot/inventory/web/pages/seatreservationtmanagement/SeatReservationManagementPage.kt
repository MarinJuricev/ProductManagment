package org.product.inventory.web.pages.seatreservationtmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.core.rememberNavigator
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.menu.MenuEvent
import org.product.inventory.web.pages.menu.MenuItemType
import org.product.inventory.web.pages.menu.MenuViewModel

@Page(Routes.seatReservationManagement)
@Composable
fun SeatReservationManagementPage() {
    val menuViewModel = rememberViewModel<MenuViewModel>()
    menuViewModel.onEvent(MenuEvent.UpdateSelectedMenuItem(MenuItemType.SeatReservation))

    val scope = rememberCoroutineScope()
    val viewModel = rememberViewModel<SeatReservationManagementViewModel>(scope)
    val navigator = rememberNavigator()
    val state by viewModel.state.collectAsState()
    val detailsData by viewModel.officeDetailsData.collectAsState()
    val deleteData by viewModel.deleteOfficeData.collectAsState()

    state.routeToNavigate?.let(navigator::navigateToRoute)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        SeatReservationManagement(
            state = state,
            detailsData = detailsData,
            deleteData = deleteData,
            onEvent = viewModel::onEvent,
        )

        state.alertMessage?.let {
            AlertMessage(
                modifier = Modifier.align(Alignment.BottomCenter),
                alertMessage = it,
            )
        }
    }
}
