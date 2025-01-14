package org.product.inventory.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import org.product.inventory.server.email.routing.emailRoutes

fun Application.configureRouting() {
    install(Resources)
    routing {
        emailRoutes()
    }
}
