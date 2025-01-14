package splash

sealed interface SplashViewEffect {
    data object NavigateToHomeScreen : SplashViewEffect
    data object NavigateToAuthScreen : SplashViewEffect
}
