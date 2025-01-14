package parking.reservation

import user.model.InventoryAppRole.Manager
import user.model.InventoryAppUser

class IsUserAbleToMakeReservation() {
    operator fun invoke(user: InventoryAppUser) = user.role >= Manager
}
