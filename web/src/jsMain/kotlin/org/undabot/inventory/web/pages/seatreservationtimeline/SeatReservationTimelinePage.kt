package org.product.inventory.web.pages.seatreservationtimeline

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
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.menu.MenuEvent
import org.product.inventory.web.pages.menu.MenuItemType
import org.product.inventory.web.pages.menu.MenuViewModel

@Page(Routes.seatReservationTimeline)
@Composable
fun SeatReservationTimelinePage() {
    val menuViewModel = rememberViewModel<MenuViewModel>()
    menuViewModel.onEvent(MenuEvent.UpdateSelectedMenuItem(MenuItemType.SeatReservation))

    val scope = rememberCoroutineScope()
    val viewModel = rememberViewModel<SeatReservationTimelineViewModel>(scope)
    val state by viewModel.state.collectAsState()
    val datesCtaStatus by viewModel.datesCtaStatus.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        SeatReservationTimeline(
            state = state,
            datesCtaStatus = datesCtaStatus,
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
