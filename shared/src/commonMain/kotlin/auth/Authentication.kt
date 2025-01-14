package auth

import arrow.core.Either
import auth.model.AuthError
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import user.model.InventoryAppUser

interface Authentication {
    suspend fun signInWithCredentialToken(
        credentialToken: String,
        accessToken: String? = null,
        email: String,
    ): Either<AuthError, InventoryAppUser>

    suspend fun signOut()
    suspend fun getCurrentUser(): Either<AuthError, InventoryAppUser>
    fun isUserLoggedIn(): Boolean
    fun observeCurrentUser(): Flow<FirebaseUser?>
}
