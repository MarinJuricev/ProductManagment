package org.product.inventory.web.pages.auth

import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes

class AuthUiMapper(
    private val dictionary: Dictionary,
) {

    fun toUiState() = AuthUiState(
        message = null,
        welcomeText = dictionary.get(StringRes.authTextWelcome),
        continueText = dictionary.get(StringRes.authTextContinue),
        loginText1 = dictionary.get(StringRes.authTextLogin1),
        loginText2 = dictionary.get(StringRes.authTextLogin2),
        googleLogo = ImageRes.googleLogo,
        MarinJuricevLogo = ImageRes.MarinJuricevLogo,
    )
}
