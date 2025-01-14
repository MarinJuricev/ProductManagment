package org.product.inventory.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import org.product.inventory.server.auth.firebase

fun Application.configureSecurity() {
    install(Authentication) {
        firebase()
    }
}
