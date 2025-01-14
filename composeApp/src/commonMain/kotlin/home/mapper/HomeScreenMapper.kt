package home.mapper

import cafe.adriel.voyager.navigator.tab.Tab
import components.QuestionDialogData
import devices.TestDevicesFeatureTab
import home.interaction.HomeScreenState.Content
import menu.model.MenuOption
import menu.model.MenuOption.CrewManagement
import menu.model.MenuOption.ParkingReservations
import menu.model.MenuOption.SeatReservation
import menu.model.MenuOption.TestDevices
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.ParkingFeatureTab
import parking.crewManagement.CrewManagementFeatureTab
import seat.SeatReservationFeatureTab
import user.model.InventoryAppRole
import user.model.InventoryAppUser

class HomeScreenMapper(
    val dictionary: Dictionary,
) {

    operator fun invoke(
        inventoryAppUser: InventoryAppUser,
        menuOptions: List<MenuOption>,
    ) = Content(
        user = inventoryAppUser,
        sideMenuOptions = getSideMenuOptions(inventoryAppUser.role, menuOptions),
        dialogData = QuestionDialogData(
            isVisible = false,
            title = dictionary.getString(MR.strings.auth_text_logout_question),
            question = dictionary.getString(MR.strings.auth_text_logout_action_question),
            negativeActionText = dictionary.getString(MR.strings.general_cancel),
            positiveActionText = dictionary.getString(MR.strings.auth_text_logout),
        ),
    )

    private fun getSideMenuOptions(
        currentUserRole: InventoryAppRole,
        menuOptions: List<MenuOption>,
    ): List<Tab> = menuOptions
        .filter { it.requiredRole <= currentUserRole }
        .map(::mapMenuOptionToFeatureTab)

    private fun mapMenuOptionToFeatureTab(menuItemOption: MenuOption): Tab = when (menuItemOption) {
        TestDevices -> TestDevicesFeatureTab
        ParkingReservations -> ParkingFeatureTab
        SeatReservation -> SeatReservationFeatureTab
        CrewManagement -> CrewManagementFeatureTab
    }
}
