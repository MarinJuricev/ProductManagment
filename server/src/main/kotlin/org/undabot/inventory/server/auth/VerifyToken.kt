package org.product.inventory.server.auth

import arrow.core.Either
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken

class VerifyToken(
    private val auth: FirebaseAuth,
) {
    operator fun invoke(token: String?): Either<VerifyTokenError, FirebaseToken> = Either.catch {
        auth.verifyIdToken(token)
    }.mapLeft {
        VerifyTokenError.InvalidToken
    }
}

sealed interface VerifyTokenError {
    data object InvalidToken : VerifyTokenError
}
