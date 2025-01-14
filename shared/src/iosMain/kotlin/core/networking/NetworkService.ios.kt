package core.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun buildHttpClient() = HttpClient(Darwin) { getClientConfig() }
