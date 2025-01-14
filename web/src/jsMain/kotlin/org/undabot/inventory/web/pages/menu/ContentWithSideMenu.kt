package org.product.inventory.web.pages.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.modifiers.onMouseOver
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.components.animation.Keyframes
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.Drawer
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.hideDrawerOnClick
import org.product.inventory.web.components.invisibleModifier
import org.product.inventory.web.components.pointerCursor
import org.product.inventory.web.components.showDrawerOnClick
import org.product.inventory.web.components.toPersistentAnimation
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.rememberNavigator
import org.product.inventory.web.core.rememberViewModel
import org.product.inventory.web.pages.menu.MenuEvent.Navigate
import org.product.inventory.web.pages.menu.MenuViewEffect.OnNavigate
import org.product.inventory.web.toCssColor

val ShiftRightKeyframes by Keyframes {
    from { Modifier.width(0.px) }
    to { Modifier.width(10.px) }
}

val ShiftLeftKeyframes by Keyframes {
    from { Modifier.width(10.px) }
    to { Modifier.width(0.px) }
}

val sideMenuStyle by ComponentStyle {
    Breakpoint.ZERO {
        invisibleModifier
    }

    Breakpoint.LG {
        Modifier.display(DisplayStyle.Flex)
            .width(DEFAULT_SIDE_MENU_WIDTH)
            .fillMaxHeight()
            .position(Position.Fixed)
            .left(0.px)
            .top(0.px)
    }
}

val mainContentStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.margin(0.percent)
    }

    Breakpoint.LG {
        Modifier.margin(left = 20.percent).overflow(Overflow.Auto)
    }
}

val drawerIconStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier
            .size(25.px)
            .margin(
                leftRight = 5.px,
                topBottom = 10.px,
            )
    }

    Breakpoint.SM {
        Modifier
            .size(30.px)
            .margin(
                leftRight = 5.px,
                topBottom = 10.px,
            )
    }

    Breakpoint.MD {
        Modifier
            .size(40.px)
            .margin(10.px)
    }

    Breakpoint.LG {
        Modifier.size(0.px).margin(0.px)
    }
}

@Composable
fun ContentWithSideMenu(
    marginVisibilityProvider: (Boolean) -> Unit,
    sideMenuContent: @Composable (
        (Boolean) -> Unit,
    ) -> Unit = { DefaultSideMenu(it) },
    mainContent: @Composable (Modifier) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        sideMenuContent {
            marginVisibilityProvider(it)
        }

        mainContent(
            mainContentStyle.toModifier()
                .fillMaxSize()
                .background(color = MR.colors.background.toCssColor()),
        )
    }
}

@Composable
private fun DefaultSideMenu(
    marginVisibilityProvider: (Boolean) -> Unit,
) {
    val menuViewModel = rememberViewModel<MenuViewModel>()
    val navigator = rememberNavigator()
    val menuState by menuViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        menuViewModel.viewEffect.collect {
            when (it) {
                is OnNavigate -> navigator.navigateToRoute(it.route)
            }
        }
    }

    LaunchedEffect(menuState.isVisible) {
        marginVisibilityProvider(menuState.isVisible)
    }

    if (menuState.isVisible) {
        Drawer(id = DRAWER_ID) {
            Column(modifier = Modifier.fillMaxSize()) {
                DefaultSideMenuContent(
                    state = menuState,
                    onEvent = menuViewModel::onEvent,
                )
            }
        }

        DrawerToggle()

        Column(modifier = sideMenuStyle.toModifier()) {
            DefaultSideMenuContent(
                state = menuState,
                onEvent = menuViewModel::onEvent,
            )
        }
    }
}

@Composable
private fun ColumnScope.DefaultSideMenuContent(
    state: MenuState,
    onEvent: (MenuEvent) -> Unit,
) {
    ProfileInfo(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .margin(topBottom = 20.px),
        profileInfo = state.profileInfo,
    )

    state.menuItems.forEach { item ->
        MenuItem(
            menuItem = item,
            onClick = {
                onEvent(
                    Navigate(
                        menuItemType = item.type,
                    ),
                )
            },
        )
    }

    LogoutButton(
        text = state.logoutText,
        onEvent = onEvent,
    )
}

@Composable
private fun LogoutButton(
    text: String,
    modifier: Modifier = Modifier,
    onEvent: (MenuEvent) -> Unit,
) {
    var color by remember { mutableStateOf(Colors.Grey) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(topBottom = 10.px)
                .onMouseOver { color = Colors.Red }
                .onMouseLeave { color = Colors.Grey }
                .background(color = color)
                .hideDrawerOnClick()
                .clickable { onEvent(MenuEvent.Logout) },
            contentAlignment = Alignment.BottomCenter,
        ) {
            Text(
                value = text,
                modifier = Modifier.color(Colors.White),
            )
        }
    }
}

@Composable
private fun MenuItem(
    menuItem: MenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.px)
            .padding(16.px)
            .background(color = menuItem.backgroundColor)
            .hideDrawerOnClick()
            .clickable(onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .position(Position.Relative)
                .animation(
                    if (menuItem.isSelected) {
                        ShiftRightKeyframes.toPersistentAnimation()
                    } else {
                        ShiftLeftKeyframes.toPersistentAnimation()
                    },
                ),
        )

        Image(
            src = menuItem.icon,
            modifier = Modifier.margin(right = 20.px),
        )
        Text(
            value = menuItem.text,
            modifier = Modifier.color(color = menuItem.textColor),
        )
    }
}

@Composable
private fun DrawerToggle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = MR.colors.background.toCssColor())
            .fillMaxHeight(),
    ) {
        Image(
            src = ImageRes.drawerIcon,
            modifier = drawerIconStyle.toModifier()
                .align(Alignment.TopCenter)
                .showDrawerOnClick(DRAWER_ID)
                .pointerCursor(),
        )
    }
}

private val DEFAULT_SIDE_MENU_WIDTH = 20.percent
private const val DRAWER_ID = "drawerId"
