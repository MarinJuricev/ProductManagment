package org.product.inventory.web.di

import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

object DiJs : KoinComponent {
    /**
     * Retrieves a dependency from the Koin graph based on the Kotlin class type.
     * @param clazz KClass reference of the dependency type.
     * @param parameters Optional parameters for dependency construction.
     * @return An instance of the requested type.
     */
    inline fun <reified T : Any> get(parameters: List<Any> = emptyList()): T =
        getKoin().get(clazz = T::class, parameters = { parametersOf(*parameters.toTypedArray()) })
}
