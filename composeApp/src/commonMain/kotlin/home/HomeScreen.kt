package home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import components.LoadingIndicator
import components.QuestionDialog
import home.components.HomeSideMenu
import home.components.HomeTopBar
import home.interaction.HomeScreenEvent.OnLogoutCanceled
import home.interaction.HomeScreenEvent.OnLogoutClick
import home.interaction.HomeScreenEvent.OnLogoutConfirmed
import home.interaction.HomeScreenState.Content
import home.interaction.HomeScreenState.Loading
import home.interaction.HomeScreenState.Retry
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import parking.ParkingFeatureTab
import splash.SplashScreen

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: HomeScreenViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        when (val uiState = viewModel.uiState.collectAsState().value) {
            is Content -> HomeScreenContent(
                uiState = uiState,
                onLogoutClick = { viewModel.onEvent(OnLogoutClick) },
                onLogoutCanceled = { viewModel.onEvent(OnLogoutCanceled) },
                onLogoutConfirmed = {
                    viewModel.onEvent(OnLogoutConfirmed)
                    navigator.replace(SplashScreen())
                },
            )
            is Loading -> LoadingIndicator()
            is Retry -> {
                Button(onClick = {}) { Text("Retry") }
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    uiState: Content,
    onLogoutClick: () -> Unit,
    onLogoutConfirmed: () -> Unit,
    onLogoutCanceled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState(drawerState)
    val scope = rememberCoroutineScope()

    if (uiState.dialogData.isVisible) {
        QuestionDialog(
            questionDialogData = uiState.dialogData,
            onNegativeActionClick = onLogoutCanceled,
            onPositiveActionClick = onLogoutConfirmed,
            onDismissRequest = onLogoutCanceled,
        )
    }

    TabNavigator(ParkingFeatureTab) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            drawerContent = {
                HomeSideMenu(
                    options = uiState.sideMenuOptions,
                    onItemClick = { scope.launch { drawerState.close() } },
                )
            },
            topBar = {
                HomeTopBar(
                    user = uiState.user,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onLogoutClick = onLogoutClick,
                )
            },
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .background(colorResource(MR.colors.background.resourceId)),
            ) {
                CurrentTab()
            }
        }
    }
}
