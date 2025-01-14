package core.model

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import user.model.InventoryAppRole

open class DashboardOption(
    open val requiredRole: InventoryAppRole,
    open val icon: ImageResource,
    open val description: StringResource,
    open val title: StringResource,
)
