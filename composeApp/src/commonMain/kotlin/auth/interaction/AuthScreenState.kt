package auth.interaction

import dev.icerock.moko.resources.ImageResource

data class AuthScreenState(
    val welcomeMessage: String,
    val buttonText: String,
    val separatorLineText: String,
    val authProviderName: String,
    val headerImageContentDescription: String,
    val headerImageResource: ImageResource,
    val isLoading: Boolean,
)
