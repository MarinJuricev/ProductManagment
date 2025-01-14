package org.product.inventory.utils.dictionary

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

interface Dictionary {
    fun getString(stringResource: StringResource): String
    fun getString(
        stringResource: StringResource,
        vararg args: Any,
    ): String

    fun getPluralStringResource(
        pluralsResource: PluralsResource,
        count: Int,
    ): String
}
