package user.usecase

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import core.utils.Crypto
import kotlinx.coroutines.flow.map
import user.model.InventoryAppUser
import user.model.UserError
import user.model.UserError.Unauthorized
import user.model.UserError.UnknownError
import user.repository.UserRepository

class GetUsers(
    private val userRepository: UserRepository,
    private val authentication: Authentication,
    private val crypto: Crypto,
) {
    suspend operator fun invoke(): Either<UserError, List<InventoryAppUser>> = either {
        ensure(authentication.isUserLoggedIn()) { Unauthorized }
        userRepository.getUsers()
            .mapLeft { UnknownError }
            .bind()
            .map { user -> user.copy(email = crypto.decrypt(user.email)) }
    }
}
