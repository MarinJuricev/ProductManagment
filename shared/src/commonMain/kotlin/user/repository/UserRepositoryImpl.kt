package user.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import core.model.RepositoryError
import core.utils.safeDatabaseOperation
import database.Database
import database.path.REGISTERED_USERS_PATH
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import user.model.InventoryAppUser
import user.model.UserError
import user.model.UserError.UserNotFound

internal class UserRepositoryImpl(
    private val database: Database,
) : UserRepository {
    override suspend fun updateUser(user: InventoryAppUser): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            database
                .save(
                    id = user.id,
                    path = REGISTERED_USERS_PATH,
                    data = user,
                )
        }

    override suspend fun getUserWithEmail(email: String): Either<UserNotFound, InventoryAppUser> =
        either {
            val userList = database.queryCollection(
                path = REGISTERED_USERS_PATH,
            ) { USER_EMAIL equalTo email }
            ensure(userList.isNotEmpty()) { UserNotFound }
            userList.first().data<InventoryAppUser>()
        }

    override fun observeUserWithEmail(email: String) = database.observeCollection(
        path = REGISTERED_USERS_PATH,
        query = { USER_EMAIL equalTo email },
    ).map { documents -> documents.firstOrNull()?.data<InventoryAppUser>() }

    override suspend fun storeUser(user: InventoryAppUser): Either<UserError, InventoryAppUser> = either {
        return Either.catch {
            database.save(
                id = user.id,
                path = REGISTERED_USERS_PATH,
                data = user,
            )
            user
        }.mapLeft { error ->
            return error.message?.let { errorMessage ->
                UserError.StoreUserError(errorMessage).left()
            } ?: UserError.UnknownError.left()
        }
    }

    override fun observeUsers(): Flow<List<InventoryAppUser>> = database.observeCollection(
        path = REGISTERED_USERS_PATH,
    ).map { list -> list.map { document -> document.data<InventoryAppUser>() } }

    override suspend fun getUsers(): Either<RepositoryError, List<InventoryAppUser>> = safeDatabaseOperation {
        database.getCollection(
            path = REGISTERED_USERS_PATH,
        ).map { list -> list.data<InventoryAppUser>() }
    }

    override fun observeCurrentUser(id: String): Flow<InventoryAppUser> = database.observeDocument(
        id = id,
        path = REGISTERED_USERS_PATH,
    ).map { document -> document.data<InventoryAppUser>() }
}

private const val USER_EMAIL = "email"
