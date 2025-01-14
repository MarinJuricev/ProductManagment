package seat

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
import seat.dashboard.SeatReservationDashboardScreen

object SeatReservationFeatureTab : Tab {
    private fun readResolve(): Any = SeatReservationFeatureTab

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.side_menu_seat_reservation_title.resourceId)
            val icon = painterResource(MR.images.seat_reservation_icon.drawableResId)

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
            Navigator(SeatReservationDashboardScreen())
        }
    }
}