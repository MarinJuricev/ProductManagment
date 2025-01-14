package auth.model

import core.model.MarinJuricevError

sealed class AuthError : MarinJuricevError() {
    data object UnknownAuthError : AuthError()
    data object InvalidCredentials : AuthError()
    data object UserNotFound : AuthError()
    data object CredentialsNotReceived : AuthError()
    data object UnsupportedDomain : AuthError()
    data class StorageError(val message: String) : AuthError()
    data object Unauthorized : AuthError()
    data class InvalidGoogleCredentials(val message: String) : AuthError()
}
