package org.product.inventory.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import auth.LoginStatus
import auth.UserLoginStatus
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import core.di.initKoin
import core.model.Environment
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import org.koin.dsl.module
import org.product.inventory.BuildKonfig
import org.product.inventory.web.core.Navigator
import org.product.inventory.web.di.DiJs
import org.product.inventory.web.di.webAppModule
import org.product.inventory.web.pages.LoadingWrapper
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.pages.menu.ContentWithSideMenu
import org.product.inventory.web.pages.menu.MenuEvent.RouteChanged
import org.product.inventory.web.pages.menu.MenuViewModel
import org.product.inventory.web.service.initFirebase

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            val userLoginStatus = DiJs.get<UserLoginStatus>()
            val loginStatusState by userLoginStatus.state.collectAsState()

            var marginVisible by remember { mutableStateOf(false) }

            LoadingWrapper(
                isLoading = loginStatusState == LoginStatus.UNKNOWN,
            ) {
                ContentWithSideMenu(
                    marginVisibilityProvider = { marginVisible = it },
                    mainContent = { modifier ->
                        Box(modifier = modifier.handleMarginVisibility(marginVisible)) {
                            content()
                        }
                    },
                )
            }
        }
    }
}

@InitKobweb
fun initApp(ctx: InitKobwebContext) {
    initFirebase()

    initKoin(environment = BuildKonfig.environment.toEnvironment()) {
        modules(
            webAppModule(
                listOf(
                    module {
                        single { ctx }
                        single { window }
                    },
                ),
            ),
        )
    }

    handleNavigation()
}

private fun handleNavigation(
    context: InitKobwebContext = DiJs.get(),
    appScope: CoroutineScope = DiJs.get(),
    navigator: Navigator = DiJs.get(),
    userLoginStatus: UserLoginStatus = DiJs.get(),
    menuViewModel: MenuViewModel = DiJs.get(listOf(appScope)),
) {
    context.router.addRouteInterceptor {
        menuViewModel.onEvent(RouteChanged(path))

        val isLoggedIn = userLoginStatus.state.value == LoginStatus.LOGGED_IN
        val isLoggedOut = userLoginStatus.state.value == LoginStatus.LOGGED_OUT

        val shouldSkipAuth = isLoggedIn && (path == Routes.auth || path == Routes.home)
        val isUnauthorized = isLoggedOut && path != Routes.auth

        when {
            shouldSkipAuth -> path = Routes.parkingReservation
            isUnauthorized -> path = Routes.auth
            else -> Unit
        }
    }

    userLoginStatus.state
        .onEach { status ->
            when (status) {
                LoginStatus.LOGGED_OUT -> navigator.navigateToRoute(Routes.auth)
                LoginStatus.LOGGED_IN -> {
                    val currentRoute = navigator.currentRoute
                    if (currentRoute == Routes.auth || currentRoute == Routes.home) {
                        navigator.navigateToRoute(Routes.parkingReservation)
                    }
                }

                LoginStatus.UNKNOWN -> Unit
            }
        }
        .launchIn(appScope)
}

// This is a helper function that will apply a margin of 0px to the given Modifier if the marginVisible flag is false.
// Auth screen should never display side menu so we need to remove the margin of main content.
// If margin is visible we don't need to do anything since margin is already applied.
private fun Modifier.handleMarginVisibility(marginVisible: Boolean) = thenIf(
    condition = !marginVisible,
    other = Modifier.margin(0.px),
)

private fun String.toEnvironment() = when (this) {
    "PROD" -> Environment.PRODUCTION
    else -> Environment.DEVELOPMENT
}
