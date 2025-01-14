package splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import auth.AuthScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.LoadingIndicator
import home.HomeScreen
import splash.SplashViewEffect.NavigateToAuthScreen
import splash.SplashViewEffect.NavigateToHomeScreen

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: SplashScreenViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    NavigateToAuthScreen -> navigator.replace(AuthScreen())
                    NavigateToHomeScreen -> navigator.replace(HomeScreen())
                }
            }
        }
        LoadingIndicator(modifier = Modifier.fillMaxSize())
    }
}
