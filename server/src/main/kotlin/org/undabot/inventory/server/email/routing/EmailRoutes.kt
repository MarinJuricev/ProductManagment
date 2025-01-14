package org.product.inventory.server.email.routing

import arrow.core.Either.Left
import arrow.core.Either.Right
import core.model.EmailRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import org.koin.ktor.ext.get
import org.product.inventory.server.email.usecase.SendEmailUseCase
import routes.V1

fun Routing.emailRoutes(
    sendEmail: SendEmailUseCase = get(),
) {
    authenticate {
        post<V1.SendEmailRoute> {
            val data = call.receive<EmailRequestData>()
            when (sendEmail(data)) {
                is Left -> call.respondText(text = MESSAGE_EMAIL_FAILURE, status = HttpStatusCode.InternalServerError)
                is Right -> call.respondText(text = MESSAGE_EMAIL_SENT, status = HttpStatusCode.OK)
            }
        }
    }
}

private const val MESSAGE_EMAIL_SENT = "Email sent"
private const val MESSAGE_EMAIL_FAILURE = "The email was not delivered"
