package parking.role

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import user.model.InventoryAppRole
import user.usecase.ObserveCurrentUser

class ObserveScreenUnavailability(
    private val observeCurrentUser: ObserveCurrentUser,
) {

    operator fun invoke(requiredRole: InventoryAppRole) = observeCurrentUser()
        .map { it.role }
        .distinctUntilChanged()
        .filter { role -> role < requiredRole }
}
