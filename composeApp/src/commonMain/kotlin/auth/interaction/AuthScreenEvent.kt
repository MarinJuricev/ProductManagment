package auth.interaction

import arrow.core.Either
import auth.model.AuthError
import org.product.inventory.utils.GoogleCredential

sealed interface AuthScreenEvent {
    data object GoogleLoginClick : AuthScreenEvent
    data class CredentialsResultReceived(
        val result: Either<AuthError, GoogleCredential>,
    ) : AuthScreenEvent
}
