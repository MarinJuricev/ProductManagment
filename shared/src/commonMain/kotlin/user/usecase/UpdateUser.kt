package user.usecase

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import core.utils.Crypto
import user.model.InventoryAppRole.Administrator
import user.model.InventoryAppUser
import user.model.UserError
import user.model.UserError.Unauthorized
import user.repository.UserRepository

class UpdateUser(
    private val userRepository: UserRepository,
    private val authentication: Authentication,
    private val crypto: Crypto,
) {
    suspend operator fun invoke(user: InventoryAppUser): Either<UserError, Unit> = either {
        val currentUser = authentication
            .getCurrentUser()
            .mapLeft { UserError.UserNotFound }
            .bind()

        ensure(currentUser.role == Administrator) { Unauthorized }
        val encryptedUser = user.copy(email = crypto.encrypt(user.email))
        userRepository
            .updateUser(user = encryptedUser)
    }
}
