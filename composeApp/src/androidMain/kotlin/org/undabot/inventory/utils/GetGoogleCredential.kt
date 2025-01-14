package org.product.inventory.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import auth.model.AuthError
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class GetGoogleCredential(
    private val dictionary: Dictionary,
) {

    private val googleIdOption: GetSignInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(SERVER_CLIENT_ID)
            .build()
    private val request = GetCredentialRequest.Builder()
        .setCredentialOptions(listOf(googleIdOption))
        .build()

    suspend operator fun invoke(context: Context): Either<AuthError, GoogleCredential> = either {
        return Either.catch {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            val email = result.credential.data.getString(EMAIL_KEY)
            val token = result.credential.data.getString(TOKEN_KEY)
            ensureNotNull(email) { AuthError.CredentialsNotReceived }
            ensureNotNull(token) { AuthError.CredentialsNotReceived }
            GoogleCredential(email = email, token = token)
        }.mapLeft {
            return AuthError.InvalidGoogleCredentials(
                dictionary.getString(MR.strings.auth_message_invalid_google_credentials),
            ).left()
        }
    }
}

data class GoogleCredential(val email: String, val token: String)

private const val SERVER_CLIENT_ID =
    "988137395439-qgv5vpc7hvjqg1abr5905rvuj87jmgec.apps.googleusercontent.com"
private const val EMAIL_KEY = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID"
private const val TOKEN_KEY = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN"
