package core.model

import email.model.Email
import email.model.EmailRequest
import email.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class EmailRequestData(
    val recipient: String,
    val htmlText: String,
    val subject: String,
) {
    fun toEmailRequest(): EmailRequest = EmailRequest(
        messages = listOf(
            Message(
                from = Email(FACILITY_EMAIL, FACILITY),
                to = listOf(Email(recipient, recipient.substringBefore("."))),
                subject = subject,
                htmlPart = htmlText,
                cc = listOf(),
            ),
        ),
    )
}

private const val FACILITY_EMAIL = "facility@MarinJuricev.com"
private const val FACILITY = "Facility"
