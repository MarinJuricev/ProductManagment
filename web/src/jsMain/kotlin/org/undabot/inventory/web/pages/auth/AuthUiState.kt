package org.product.inventory.web.pages.auth

import dev.icerock.moko.resources.ColorResource
import org.product.inventory.web.core.ImageRes

data class AuthUiState(
    val message: Message? = null,
    val MarinJuricevLogo: String = ImageRes.MarinJuricevLogo,
    val googleLogo: String = ImageRes.googleLogo,
    val welcomeText: String = "",
    val continueText: String = "",
    val loginText1: String = "",
    val loginText2: String = "",
)

data class Message(
    val title: String,
    val description: String,
    val icon: String,
    val backgroundColor: ColorResource,
)
