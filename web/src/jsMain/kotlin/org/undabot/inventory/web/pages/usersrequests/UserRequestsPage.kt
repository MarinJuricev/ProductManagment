package org.product.inventory.web.pages.usersrequests

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

@Page(Routes.userRequests)
@Composable
fun UsersRequestsPage() {
    val scope = rememberCoroutineScope()
    val userRequestsViewModel = rememberViewModel<UserRequestsViewModel>(scope)
    val navigator = rememberNavigator()
    val userRequestsState by userRequestsViewModel.state.collectAsState()
    val detailsState by userRequestsViewModel.detailsState.collectAsState()

    userRequestsState.routeToNavigate?.let(navigator::navigateToRoute)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        UserRequests(
            state = userRequestsState,
            detailsState = detailsState,
            onEvent = userRequestsViewModel::onEvent,
        )

        userRequestsState.alertMessage?.let {
            AlertMessage(
                modifier = Modifier.align(Alignment.BottomCenter),
                alertMessage = it,
            )
        }
    }
}
