package org.product.inventory.server.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHost(LOCALHOST)
        allowHost(PRODUCTION)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(TOKEN_HEADER)
    }
}

private const val LOCALHOST = "localhost:8080"
private const val PRODUCTION = "MarinJuricev-inventory-app.firebaseapp.com"
private const val TOKEN_HEADER = "token"
