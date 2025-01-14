package org.product.inventory.web.pages.myreservations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.core.Page
import org.product.inventory.web.core.rememberNavigator
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.Routes

@Page(Routes.myReservations)
@Composable
fun MyReservationsPage() {
    val scope = rememberCoroutineScope()
    val myReservationsViewModel = rememberViewModel<MyReservationsViewModel>(scope)
    val navigator = rememberNavigator()
    val myReservationsState by myReservationsViewModel.state.collectAsState()
    val selectedReservation by myReservationsViewModel.selectedReservation.collectAsState()

    myReservationsState.routeToNavigate?.let(navigator::navigateToRoute)

    MyReservations(
        state = myReservationsState,
        selectedReservation = selectedReservation,
        onEvent = myReservationsViewModel::onEvent,
    )
}
