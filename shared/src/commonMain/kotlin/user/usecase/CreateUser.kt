package user.usecase

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import core.utils.Crypto
import core.utils.IsEmailValid
import user.model.CreateUserError
import user.model.CreateUserError.CreateUserFailed
import user.model.CreateUserError.DuplicatedUser
import user.model.CreateUserError.InvalidEmail
import user.model.InventoryAppUser
import user.repository.UserRepository

class CreateUser(
    private val userRepository: UserRepository,
    private val isEmailValid: IsEmailValid,
    private val crypto: Crypto,
) {
    suspend operator fun invoke(
        user: InventoryAppUser,
    ): Either<CreateUserError, InventoryAppUser> = either {
        ensure(isEmailValid(user.email)) { InvalidEmail }
        checkIfUserAlreadyExists(email = user.email)
        userRepository
            .storeUser(user = user.copy(email = crypto.encrypt(user.email)))
            .mapLeft { CreateUserFailed }
            .bind()
    }

    private suspend fun Raise<DuplicatedUser>.checkIfUserAlreadyExists(email: String) = userRepository
        .getUserWithEmail(email = crypto.encrypt(email))
        .onRight { raise(DuplicatedUser) }
}
