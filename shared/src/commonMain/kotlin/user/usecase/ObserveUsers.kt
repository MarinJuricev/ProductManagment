package user.usecase

import core.utils.Crypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import user.model.InventoryAppUser
import user.repository.UserRepository

class ObserveUsers(
    private val repository: UserRepository,
    private val observeCurrentUser: ObserveCurrentUser,
    private val crypto: Crypto,
) {
    operator fun invoke(): Flow<List<InventoryAppUser>> = observeCurrentUser()
        .flatMapLatest {
            repository.observeUsers()
                .map { list -> list.map { user -> user.copy(email = crypto.decrypt(user.email)) } }
        }
}
