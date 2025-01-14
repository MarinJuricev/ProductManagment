package org.product.inventory.web.pages.menu

import com.varabyte.kobweb.compose.ui.graphics.Color
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.pages.Routes

data class MenuState(
    val profileInfo: ProfileInfo = ProfileInfo(),
    val menuItems: List<MenuItem> = emptyList(),
    val isVisible: Boolean = false,
    val logoutText: String = "",
)

data class MenuItem(
    val icon: String,
    val text: String,
    val backgroundColor: Color.Rgb,
    val textColor: Color.Rgb,
    val isSelected: Boolean,
    val type: MenuItemType,
)

enum class MenuItemType(val route: String) {
    TestDevices(Routes.testDevices),
    ParkingReservations(Routes.parkingReservation),
    SeatReservation(Routes.seatReservation),
    CrewManagement(Routes.crewManagement),
}
