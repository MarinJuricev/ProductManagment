package parking.emailTemplates.models

data class EmailTemplatesUiModel(
    val approvedLabel: String,
    val declinedLabel: String,
    val submittedLabel: String,
    val canceledLabel: String,
    val canceledByAdminLabel: String,
    val saveButtonLabel: String,
)
