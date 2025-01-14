package email

import arrow.core.Either
import core.model.EmailRequestData
import core.model.NetworkError

interface EmailService {

    suspend fun sendEmail(
        emailRequest: EmailRequestData,
    ): Either<NetworkError, Unit>
}
