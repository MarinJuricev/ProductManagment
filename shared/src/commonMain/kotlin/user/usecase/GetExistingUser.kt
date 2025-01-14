package user.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import core.utils.Crypto
import core.utils.IsEmailValid
import user.model.InventoryAppUser
import user.model.UserError
import user.repository.UserRepository

class GetExistingUser(
    private val userRepository: UserRepository,
    private val isEmailValid: IsEmailValid,
    private val crypto: Crypto,
) {
    suspend operator fun invoke(
        email: String?,
    ): Either<UserError, InventoryAppUser> = either {
        ensure(!email.isNullOrEmpty()) { UserError.InvalidEmail }
        ensure(isEmailValid(email)) { UserError.InvalidEmail }
        return when (val existingUser = userRepository.getUserWithEmail(crypto.encrypt(email))) {
            is Either.Left -> UserError.UserNotFound.left()
            is Either.Right -> {
                val user = existingUser.value
                return@either existingUser.value.copy(email = crypto.decrypt(user.email))
            }
        }
    }
}
