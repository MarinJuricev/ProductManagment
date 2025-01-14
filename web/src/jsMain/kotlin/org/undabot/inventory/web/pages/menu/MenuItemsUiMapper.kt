package org.product.inventory.web.pages.menu

import com.varabyte.kobweb.compose.ui.graphics.Colors
import menu.model.MenuOption
import org.product.inventory.shared.MR
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.menu.MenuItemType.CrewManagement
import org.product.inventory.web.pages.menu.MenuItemType.ParkingReservations
import org.product.inventory.web.pages.menu.MenuItemType.SeatReservation
import org.product.inventory.web.pages.menu.MenuItemType.TestDevices
import org.product.inventory.web.toCssColor

class MenuItemsUiMapper(
    val dictionary: Dictionary,
) {

    fun map(
        type: MenuItemType,
        menuItemOptions: List<MenuOption>,
    ) = menuItemOptions
        .map { menuOption ->
            val itemType = mapMenuOptionToMenuItemType(menuOption)
            val isSelected = (type == itemType)

            MenuItem(
                icon = when (itemType) {
                    TestDevices -> if (isSelected) ImageRes.testDevicesSelected else ImageRes.testDevices
                    ParkingReservations -> if (isSelected) ImageRes.parkingReservationSelected else ImageRes.parkingReservation
                    SeatReservation -> if (isSelected) ImageRes.seatReservationSelected else ImageRes.seatReservation
                    CrewManagement -> if (isSelected) ImageRes.crewManagementSelected else ImageRes.crewManagement
                },
                backgroundColor = if (isSelected) MR.colors.secondary.toCssColor() else Colors.White,
                text = when (itemType) {
                    TestDevices -> dictionary.get(StringRes.menuTestDevices)
                    ParkingReservations -> dictionary.get(StringRes.menuParkingReservation)
                    SeatReservation -> dictionary.get(StringRes.menuSeatReservation)
                    CrewManagement -> dictionary.get(StringRes.crewManagementTitle)
                },
                textColor = if (isSelected) Colors.White else Colors.Black,
                isSelected = isSelected,
                type = itemType,
            )
        }

    private fun mapMenuOptionToMenuItemType(menuOption: MenuOption): MenuItemType = when (menuOption) {
        MenuOption.TestDevices -> TestDevices
        MenuOption.ParkingReservations -> ParkingReservations
        MenuOption.SeatReservation -> SeatReservation
        MenuOption.CrewManagement -> CrewManagement
    }
}
