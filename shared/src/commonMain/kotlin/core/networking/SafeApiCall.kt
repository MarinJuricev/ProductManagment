package core.networking

import arrow.core.Either
import core.model.NetworkError
import io.ktor.client.plugins.ResponseException

suspend fun <SuccessModel> safeApiCall(
    apiCall: suspend () -> SuccessModel,
): Either<NetworkError, SuccessModel> = Either.catch { apiCall() }.mapLeft { error ->
    if (error is ResponseException) {
        NetworkError.BackendError(
            responseCode = error.response.status.value,
            errorMessage = error.message,
        )
    } else {
        NetworkError.UnknownNetworkError
    }
}
