package parking.templates.model

import kotlinx.serialization.Serializable

@Serializable
data class Template(
    val id: String,
    val text: String,
    val title: String,
    val status: TemplateStatus,
)

@Serializable
sealed class TemplateStatus {
    @Serializable
    data object Submitted : TemplateStatus()

    @Serializable
    data object Approved : TemplateStatus()

    @Serializable
    data object Declined : TemplateStatus()

    @Serializable
    data object Canceled : TemplateStatus()

    @Serializable
    data object CanceledByAdmin : TemplateStatus()
}
