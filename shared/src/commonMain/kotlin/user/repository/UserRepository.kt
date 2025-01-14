package user.repository

import arrow.core.Either
import core.model.RepositoryError
import kotlinx.coroutines.flow.Flow
import user.model.InventoryAppUser
import user.model.UserError

interface UserRepository {
    suspend fun updateUser(user: InventoryAppUser): Either<RepositoryError, Unit>

    suspend fun getUserWithEmail(email: String): Either<UserError, InventoryAppUser>

    suspend fun storeUser(user: InventoryAppUser): Either<UserError, InventoryAppUser>

    fun observeUsers(): Flow<List<InventoryAppUser>>

    suspend fun getUsers(): Either<RepositoryError, List<InventoryAppUser>>

    fun observeCurrentUser(id: String): Flow<InventoryAppUser>

    fun observeUserWithEmail(email: String): Flow<InventoryAppUser?>
}
