package core.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun buildHttpClient() = HttpClient(OkHttp) { getClientConfig() }
