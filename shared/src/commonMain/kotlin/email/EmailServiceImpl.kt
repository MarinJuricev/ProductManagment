package email

import arrow.core.Either
import core.model.EmailRequestData
import core.model.NetworkError
import core.networking.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import routes.V1

class EmailServiceImpl(
    private val httpClient: HttpClient,
) : EmailService {

    override suspend fun sendEmail(
        emailRequest: EmailRequestData,
    ): Either<NetworkError, Unit> = safeApiCall {
        httpClient.post(V1.SendEmailRoute()) { setBody(emailRequest) }
    }
}
