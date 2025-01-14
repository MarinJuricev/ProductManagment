@file:OptIn(BetaInteropApi::class)

package core.di

import core.logger.LogLevel
import core.logger.UbLogger
import core.utils.DateConverter
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCObject
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    factory { DateConverter() }
}

/**
 * * Use for getting dependencies from the Koin dependency graph
 *  * @param type Class/Protocol/Interface you wish to get
 *  * @param parameters List of runtime parameters needed to successfully construct the desired dependency
 */
object Di : KoinComponent {

    private val logger: UbLogger by inject()

    fun get(
        type: ObjCObject,
        parameters: List<Any>,
    ): Any? = getKoin().get(
        clazz = when (type) {
            is ObjCProtocol -> getOriginalKotlinClass(type)!!
            is ObjCClass -> getOriginalKotlinClass(type)!!
            else -> {
                logger.log(LogLevel.DEBUG, "Failed to resolve $type")
                error("Cannot convert $type to KClass<*>")
            }
        },
        qualifier = null,
    ) { parametersOf(*parameters.toTypedArray()) }
}
