package auth

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import auth.model.AuthError
import auth.model.AuthError.InvalidCredentials
import auth.model.AuthError.StorageError
import auth.model.AuthError.Unauthorized
import auth.model.AuthError.UnknownAuthError
import auth.model.AuthError.UserNotFound
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider.credential
import kotlinx.coroutines.flow.Flow
import user.model.InventoryAppUser
import user.model.UserError
import user.usecase.GetExistingUser
import user.usecase.StoreUserIfNotExists

internal class AuthenticationImpl(
    private val firebaseAuth: FirebaseAuth,
    private val storeUserIfNotExists: StoreUserIfNotExists,
    private val getExistingUser: GetExistingUser,
) : Authentication {
    override suspend fun signInWithCredentialToken(
        credentialToken: String,
        accessToken: String?,
        email: String,
    ): Either<AuthError, InventoryAppUser> = either {
        return Either.catch {
            ensure(email.endsWith(MarinJuricev_DOMAIN)) { AuthError.UnsupportedDomain }
            val user = firebaseAuth.signInWithCredential(
                credential(idToken = credentialToken, accessToken),
            ).user
            ensureNotNull(user) { UserNotFound }
            getExistingOrStoreNewUser(user).bind()
        }.mapLeft { it.toAuthError() }
    }

    override suspend fun signOut() = firebaseAuth.signOut()
    override suspend fun getCurrentUser(): Either<AuthError, InventoryAppUser> = either {
        val firebaseUser = firebaseAuth.currentUser
        ensureNotNull(firebaseUser) { UserNotFound }
        getExistingUser(firebaseUser.email).mapLeft { it.toAuthError() }.bind()
    }

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    private suspend fun getExistingOrStoreNewUser(user: FirebaseUser): Either<AuthError, InventoryAppUser> =
        storeUserIfNotExists(user.email, user.photoURL).mapLeft { it.toAuthError() }

    private fun Throwable.toAuthError() = when (this) {
        is FirebaseAuthInvalidCredentialsException -> InvalidCredentials
        is FirebaseAuthInvalidUserException -> UserNotFound
        else -> UnknownAuthError
    }

    private fun UserError.toAuthError() = when (this) {
        is UserError.InvalidEmail -> InvalidCredentials
        is UserError.UnknownError -> UnknownAuthError
        is UserError.UserNotFound -> UserNotFound
        is UserError.StoreUserError -> StorageError(this.message)
        is UserError.Unauthorized -> Unauthorized
    }

    override fun observeCurrentUser(): Flow<FirebaseUser?> = firebaseAuth.authStateChanged
}

internal const val MarinJuricev_DOMAIN = "@MarinJuricev.com"
