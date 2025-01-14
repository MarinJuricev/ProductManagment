package splash

import auth.Authentication
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import splash.SplashViewEffect.NavigateToAuthScreen
import splash.SplashViewEffect.NavigateToHomeScreen

class SplashScreenViewModel(
    authentication: Authentication,
) : ScreenModel {

    private val _viewEffect = Channel<SplashViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<SplashViewEffect> = _viewEffect.receiveAsFlow()

    init {
        screenModelScope.launch {
            if (authentication.isUserLoggedIn()) {
                _viewEffect.send(NavigateToHomeScreen)
            } else {
                _viewEffect.send(NavigateToAuthScreen)
            }
        }
    }
}
