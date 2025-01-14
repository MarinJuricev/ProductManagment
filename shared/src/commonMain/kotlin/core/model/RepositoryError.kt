package core.model

sealed class RepositoryError

sealed class DatabaseOperationError : RepositoryError() {
    data class UnknownError(val message: String) : DatabaseOperationError()
}

sealed class NetworkError : RepositoryError() {
    data object UnknownNetworkError : NetworkError()
    data class BackendError(val responseCode: Int, val errorMessage: String?) : NetworkError()
}
