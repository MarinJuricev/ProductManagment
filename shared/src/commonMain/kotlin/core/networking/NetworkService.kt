package core.networking

import io.ktor.client.HttpClient

expect fun buildHttpClient(): HttpClient
