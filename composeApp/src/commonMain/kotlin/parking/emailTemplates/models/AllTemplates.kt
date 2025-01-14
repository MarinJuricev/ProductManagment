package parking.emailTemplates.models

import parking.templates.model.Template

data class AllTemplates(
    val submitted: Template? = null,
    val approved: Template? = null,
    val declined: Template? = null,
    val canceled: Template? = null,
    val canceledByAdmin: Template? = null,
)
