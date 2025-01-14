package org.product.inventory.web.pages.newrequest

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

@Page(Routes.newRequest)
@Composable
fun NewRequestPage() {
    val scope = rememberCoroutineScope()
    val newRequestViewModel = rememberViewModel<NewRequestViewModel>(scope)
    val navigator = rememberNavigator()
    val newRequestState by newRequestViewModel.state.collectAsState()
    val userListState by newRequestViewModel.userListState.collectAsState()
    val multipleSelectionNewRequestState by newRequestViewModel.multiDateSelectionState.collectAsState()

    newRequestState.routeToNavigate?.let(navigator::navigateToRoute)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        NewRequest(
            state = newRequestState,
            userListState = userListState,
            multiDateSelectionState = multipleSelectionNewRequestState,
            onEvent = newRequestViewModel::onEvent,
        )

        newRequestState.alertMessage?.let {
            AlertMessage(
                modifier = Modifier.align(Alignment.BottomCenter),
                alertMessage = it,
            )
        }
    }
}
