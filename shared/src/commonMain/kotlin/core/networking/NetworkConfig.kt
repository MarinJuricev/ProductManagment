package core.networking

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun <T : HttpClientEngineConfig> HttpClientConfig<T>.getClientConfig(
    baseUrl: String = BACKEND_BASE_URL,
) {
    install(Resources)
    defaultRequest {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        url(baseUrl)
    }
    install(HttpTimeout) { requestTimeoutMillis = TIMEOUT_MS }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }
}

private const val BACKEND_BASE_URL = "https://inventory-kmp.MarinJuricev.dev"
private const val TIMEOUT_MS = 60_000L
