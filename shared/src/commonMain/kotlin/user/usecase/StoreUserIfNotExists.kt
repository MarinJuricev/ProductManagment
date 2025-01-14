package user.usecase

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import core.utils.Crypto
import core.utils.IsEmailValid
import core.utils.UUID
import user.model.CreateUserError
import user.model.InventoryAppRole
import user.model.InventoryAppUser
import user.model.UserError
import user.repository.UserRepository

class StoreUserIfNotExists(
    private val userRepository: UserRepository,
    private val createUser: CreateUser,
    private val isEmailValid: IsEmailValid,
    private val crypto: Crypto,
) {
    suspend operator fun invoke(
        email: String?,
        profileImageUrl: String?,
    ): Either<UserError, InventoryAppUser> = either {
        ensure(!email.isNullOrEmpty()) { UserError.InvalidEmail }
        ensure(isEmailValid(email)) { UserError.InvalidEmail }
        return when (val existingUser = userRepository.getUserWithEmail(crypto.encrypt(email))) {
            is Either.Left -> {
                val user = InventoryAppUser(
                    id = UUID(),
                    email = email,
                    profileImageUrl = profileImageUrl.orEmpty(),
                    role = InventoryAppRole.User,
                    hasPermanentGarageAccess = false,
                )
                createUser(user = user)
                    .mapLeft { it.toUserError() }
                    .map {
                        it.copy(
                            email = crypto.decrypt(it.email),
                        )
                    }
            }

            is Either.Right -> {
                existingUser.value.copy(email = crypto.decrypt(existingUser.value.email)).right()
            }
        }
    }

    private fun CreateUserError.toUserError(): UserError = when (this) {
        is CreateUserError.DuplicatedUser -> UserError.StoreUserError("User with this email already exists")
        is CreateUserError.CreateUserFailed -> UserError.StoreUserError("User creation failed")
        is CreateUserError.InvalidEmail -> UserError.InvalidEmail
    }
}
