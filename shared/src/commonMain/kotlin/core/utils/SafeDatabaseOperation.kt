package core.utils

import arrow.core.Either
import core.model.DatabaseOperationError

suspend fun <SuccessType : Any> safeDatabaseOperation(
    databaseOperation: suspend () -> SuccessType,
): Either<DatabaseOperationError, SuccessType> = Either.catch {
    databaseOperation()
}.mapLeft {
    DatabaseOperationError.UnknownError(it.message.orEmpty())
}
