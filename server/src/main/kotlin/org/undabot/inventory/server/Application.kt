package org.product.inventory.server

import io.ktor.server.application.Application
import org.product.inventory.server.di.configureKoin
import org.product.inventory.server.plugins.configureDatabases
import org.product.inventory.server.plugins.configureHTTP
import org.product.inventory.server.plugins.configureMonitoring
import org.product.inventory.server.plugins.configureRouting
import org.product.inventory.server.plugins.configureSecurity
import org.product.inventory.server.plugins.configureSerialization
import org.product.inventory.server.plugins.configureStatusPage

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureStatusPage()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
