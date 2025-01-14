package core.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

actual fun buildHttpClient() = HttpClient(Js) { getClientConfig() }
