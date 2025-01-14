package email.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailRequest(
    @SerialName("Messages") val messages: List<Message>,
)

@Serializable
data class Message(
    @SerialName("from") val from: Email,
    @SerialName("To") val to: List<Email>,
    @SerialName("Subject") val subject: String,
    @SerialName("HTMLPart") val htmlPart: String,
    @SerialName("Cc") val cc: List<Email>,
)

@Serializable
data class Email(
    @SerialName("Email") val email: String,
    @SerialName("Name") val name: String,
)
