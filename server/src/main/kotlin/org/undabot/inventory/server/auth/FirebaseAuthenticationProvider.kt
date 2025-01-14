package org.product.inventory.server.auth

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject
import org.product.inventory.server.auth.FirebaseAuthenticationProvider.Configuration

private class FirebaseAuthenticationProvider(config: Config) : AuthenticationProvider(config) {

    class Configuration(name: String?) : Config(name)

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val token = call.request.headers[TOKEN]
        val verifyToken: VerifyToken by context.call.application.inject()

        when (val result = verifyToken(token)) {
            is Left -> context.challenge(
                key = FIREBASE_AUTH,
                cause = AuthenticationFailedCause.InvalidCredentials,
            ) { challenge, _ ->
                call.respond(status = HttpStatusCode.Unauthorized, message = MESSAGE_UNAUTHORIZED)
                challenge.complete()
            }

            is Right -> context.principal(TokenPrincipal(result.value))
        }
    }
}

fun AuthenticationConfig.firebase(name: String? = null) {
    val provider = FirebaseAuthenticationProvider(Configuration(name))
    register(provider)
}

private const val FIREBASE_AUTH = "FirebaseAuth"
private const val TOKEN = "token"
private const val MESSAGE_UNAUTHORIZED = "Unauthorized access"
