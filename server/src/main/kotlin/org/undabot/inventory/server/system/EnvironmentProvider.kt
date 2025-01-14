package org.product.inventory.server.system

fun interface EnvironmentProvider {

    fun get(key: String): String?
}
