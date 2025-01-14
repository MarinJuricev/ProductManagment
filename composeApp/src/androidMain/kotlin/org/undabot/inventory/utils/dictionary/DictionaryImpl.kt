package org.product.inventory.utils.dictionary

import android.content.Context
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

class DictionaryImpl(
    private val context: Context,
) : Dictionary {
    override fun getString(stringResource: StringResource) =
        StringResource(stringResource.resourceId).getString(context)

    override fun getString(
        stringResource: StringResource,
        vararg args: Any,
    ) =
        StringDesc.ResourceFormatted(stringResource, args.asList()).toString(context)

    override fun getPluralStringResource(
        pluralsResource: PluralsResource,
        count: Int,
    ) =
        StringDesc.PluralFormatted(pluralsResource, count, count).toString(context = context)
}
