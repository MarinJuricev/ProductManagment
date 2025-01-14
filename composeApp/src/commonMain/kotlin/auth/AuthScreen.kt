package auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import auth.interaction.AuthScreenEvent
import auth.interaction.AuthScreenState
import auth.interaction.AuthViewEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.BodySmallText
import components.Image
import components.ImageType.Resource
import components.LoadingIndicator
import components.SocialLoginButton
import components.TextualDivider
import home.HomeScreen
import org.koin.compose.koinInject
import org.product.inventory.SurfaceColor
import org.product.inventory.shared.MR
import org.product.inventory.utils.GetGoogleCredential

class AuthScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: AuthScreenViewModel = koinScreenModel()
        val getGoogleCredential: GetGoogleCredential = koinInject()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    is AuthViewEffect.ShowGoogleLogin -> {
                        viewModel.onEvent(
                            AuthScreenEvent.CredentialsResultReceived(
                                getGoogleCredential(context),
                            ),
                        )
                    }

                    is AuthViewEffect.NavigateToHomeScreen -> navigator.replace(
                        HomeScreen(),
                    )

                    is AuthViewEffect.ShowError -> Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
        AuthScreenContent(
            uiState = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
private fun AuthScreenContent(
    modifier: Modifier = Modifier,
    uiState: AuthScreenState,
    onEvent: (AuthScreenEvent) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .clip(RoundedCornerShape(34.dp))
                .background(SurfaceColor)
                .padding(horizontal = 22.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BodySmallText(text = uiState.welcomeMessage, color = MR.colors.textLight)
            Image(
                imageType = Resource(MR.images.product_logo),
                contentDescription = uiState.headerImageContentDescription,
                modifier = Modifier.padding(top = 16.dp),
            )
            TextualDivider(
                modifier = Modifier.padding(vertical = 54.dp),
                text = uiState.separatorLineText,
            )
            SocialLoginButton(
                providerIcon = uiState.headerImageResource,
                text = uiState.buttonText,
                providerName = uiState.authProviderName,
            ) { onEvent(AuthScreenEvent.GoogleLoginClick) }
        }

        if (uiState.isLoading) {
            LoadingIndicator()
        }
    }
}
