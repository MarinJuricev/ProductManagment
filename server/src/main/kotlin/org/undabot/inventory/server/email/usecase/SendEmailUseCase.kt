package org.product.inventory.server.email.usecase

import core.model.EmailRequestData
import core.networking.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.AuthScheme
import org.product.inventory.server.system.EnvironmentProvider

class SendEmailUseCase(
    private val client: HttpClient,
    private val environmentProvider: EnvironmentProvider,
) {

    suspend operator fun invoke(data: EmailRequestData) = safeApiCall {
        client.post(SEND_EMAIL_ENDPOINT) {
            headers {
                append(
                    HttpHeaders.Authorization,
                    "${AuthScheme.Basic} ${environmentProvider.get(TOKEN)}",
                )
            }
            setBody(data.toEmailRequest())
        }
    }
}

private const val TOKEN = "EMAIL_SERVICE_API_KEY"
private const val SEND_EMAIL_ENDPOINT = "send"
