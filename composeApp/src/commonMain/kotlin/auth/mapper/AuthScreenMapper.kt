package auth.mapper

import auth.interaction.AuthScreenState
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class AuthScreenMapper(
    private val dictionary: Dictionary,
) {
    operator fun invoke() = AuthScreenState(
        welcomeMessage = dictionary.getString(MR.strings.auth_text_welcome).uppercase(),
        buttonText = dictionary.getString(MR.strings.auth_text_login1),
        authProviderName = dictionary.getString(MR.strings.auth_text_login2),
        separatorLineText = dictionary.getString(MR.strings.auth_text_continue),
        headerImageContentDescription = dictionary.getString(MR.strings.app_name),
        headerImageResource = MR.images.google_logo,
        isLoading = false,
    )
}
