package user.usecase

import auth.Authentication
import core.utils.Crypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import user.model.InventoryAppUser
import user.repository.UserRepository

class ObserveCurrentUser(
    private val repository: UserRepository,
    private val authentication: Authentication,
    private val crypto: Crypto,
) {
    operator fun invoke(): Flow<InventoryAppUser> = authentication.observeCurrentUser()
        .mapNotNull { it?.email }
        .map(crypto::encrypt)
        .flatMapLatest(repository::observeUserWithEmail)
        .filterNotNull()
        .map { user -> user.copy(email = crypto.decrypt(user.email)) }
}
