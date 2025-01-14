package seat.timeline.model

import user.model.InventoryAppUser

sealed interface InventoryUserUi {
    data object Loading : InventoryUserUi
    data object Error : InventoryUserUi
    data class User(val user: InventoryAppUser) : InventoryUserUi
}
