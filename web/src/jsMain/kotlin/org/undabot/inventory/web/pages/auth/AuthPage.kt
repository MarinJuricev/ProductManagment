package org.product.inventory.web.pages.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.graphics.Image
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.ContentBetweenLines
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.clickable
import org.product.inventory.web.core.FontRes
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.toCssColor

@Page(Routes.auth)
@Composable
fun AuthPage() {
    val authViewModel = rememberViewModel<AuthViewModel>()
    val authUiState by authViewModel.state.collectAsState()

    AuthContent(
        state = authUiState,
        onLoginClick = authViewModel::signIn,
    )
}

@Composable
fun AuthContent(
    state: AuthUiState,
    onLoginClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MR.colors.background.toCssColor()),
        contentAlignment = Alignment.Center,
    ) {
        state.message?.let {
            MessageContent(
                message = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.px)
                    .align(Alignment.TopStart)
                    .background(color = it.backgroundColor.toCssColor()),
            )
        }

        Column(
            modifier = Modifier
                .background(color = Colors.White)
                .padding(50.px)
                .borderRadius(34.px),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginText(value = state.welcomeText.uppercase())

            Image(
                src = ImageRes.MarinJuricevLogo,
                modifier = Modifier.margin(top = 50.px).size(70.percent),
            )

            ContentBetweenLines(
                modifier = Modifier.fillMaxWidth().margin(top = 70.px),
                lineColor = MR.colors.background.toCssColor(),
            ) {
                LoginText(
                    modifier = Modifier
                        .padding(leftRight = 10.px)
                        .background(color = Colors.White),
                    value = state.continueText,
                )
            }

            GoogleLoginButton(
                state = state,
                modifier = Modifier
                    .margin(top = 60.px)
                    .fillMaxWidth()
                    .border(
                        width = 2.px,
                        color = MR.colors.login_button_border.toCssColor(),
                        style = LineStyle.Solid,
                    )
                    .borderRadius(13.px)
                    .padding(8.px)
                    .clickable(onLoginClick),
            )
        }
    }
}

@Composable
private fun GoogleLoginButton(
    state: AuthUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(50.px).margin(right = 20.px),
            src = ImageRes.googleLogo,
        )

        LoginText(value = state.loginText1)
        LoginText(
            value = " ${state.loginText2}",
            fontWeight = FontWeight.Bold,
            color = MR.colors.login_google_button_text.toCssColor(),
        )
    }
}

@Composable
private fun LoginText(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = MR.colors.login_button_text.toCssColor(),
    fontSize: CSSLengthOrPercentageNumericValue = 16.px,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: String = FontRes.Montserrat,
) {
    Text(
        value = value,
        modifier = modifier
            .fontFamily(fontFamily)
            .color(color)
            .fontSize(fontSize)
            .fontWeight(fontWeight),
    )
}

@Composable
private fun MessageContent(
    message: Message,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(value = message.title)
        Text(value = message.description)
    }
}
