package org.product.inventory.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception caught", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = InventoruPlaceHolderError(
                    message = "Unexpected error occurred, please try again later.",
                    errors = setOf("Stacktrace: $cause"),
                ),
            )
        }
    }
}

@Serializable
private data class InventoruPlaceHolderError(
    val message: String,
    val errors: Set<String>,
)
