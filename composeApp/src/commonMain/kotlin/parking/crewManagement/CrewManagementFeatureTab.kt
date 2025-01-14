package parking.crewManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.product.inventory.shared.MR

object CrewManagementFeatureTab : Tab {

    private fun readResolve(): Any = CrewManagementFeatureTab

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.parking_reservation_crew_management_title.resourceId)
            val icon = painterResource(MR.images.crew_management_icon.drawableResId)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        Column(modifier = Modifier.fillMaxSize()) {
            Navigator(CrewManagementScreen())
        }
    }
}
