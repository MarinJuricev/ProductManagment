package org.product.inventory.web.pages.emailtemplates

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

@Page(Routes.emailTemplates)
@Composable
fun EmailTemplatesPage() {
    val scope = rememberCoroutineScope()
    val viewModel = rememberViewModel<EmailTemplatesViewModel>(scope)
    val navigator = rememberNavigator()
    val state by viewModel.state.collectAsState()

    state.routeToNavigate?.let(navigator::navigateToRoute)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        EmailTemplates(
            state = state,
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
