package menu.model

import user.model.InventoryAppRole

enum class MenuOption(
    val requiredRole: InventoryAppRole,
) {
    TestDevices(InventoryAppRole.User),
    ParkingReservations(InventoryAppRole.User),
    SeatReservation(InventoryAppRole.User),
    CrewManagement(InventoryAppRole.Administrator),
}
