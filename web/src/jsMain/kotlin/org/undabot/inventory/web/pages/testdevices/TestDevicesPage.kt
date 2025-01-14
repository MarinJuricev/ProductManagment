package org.product.inventory.web.pages.testdevices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import org.product.inventory.web.components.Text
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.menu.MenuEvent
import org.product.inventory.web.pages.menu.MenuItemType
import org.product.inventory.web.pages.menu.MenuViewModel

@Page(Routes.testDevices)
@Composable
fun TestDevicesPage() {
    val menuViewModel = rememberViewModel<MenuViewModel>()
    menuViewModel.onEvent(MenuEvent.UpdateSelectedMenuItem(MenuItemType.TestDevices))

    val scope = rememberCoroutineScope()
    val testDevicesViewModel = rememberViewModel<TestDevicesViewModel>(scope)
    val testDevicesState by testDevicesViewModel.state.collectAsState()

    TestDevicesContent(
        testDevicesState = testDevicesState,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun TestDevicesContent(
    testDevicesState: TestDevicesState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Text(
            value = testDevicesState.text,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
