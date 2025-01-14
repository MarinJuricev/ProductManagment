package devices

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import components.BodyLargeText

class TestDevicesDashboardScreen : Screen {
    @Composable
    override fun Content() {
        BodyLargeText("Test Devices Screen")
    }
}
