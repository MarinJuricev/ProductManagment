package core.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun buildHttpClient(): HttpClient = HttpClient(OkHttp) {
    getClientConfig(baseUrl = EMAIL_SERVICE_BASE_URL)
}

private const val EMAIL_SERVICE_BASE_URL = "https://api.mailjet.com/v3.1/"
