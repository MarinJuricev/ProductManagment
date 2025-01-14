package org.product.inventory.web.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.navigation.Router
import org.product.inventory.web.di.DiJs
import org.w3c.dom.Window

interface Navigator {

    val currentRoute: String

    fun navigateToRoute(route: String)
}

class NavigatorImpl(
    private val router: Router,
    private val window: Window,
) : Navigator {

    override val currentRoute: String
        get() = window.location.pathname

    override fun navigateToRoute(route: String) {
        router.navigateTo(pathQueryAndFragment = route)
    }
}

@Composable
fun rememberNavigator() = remember { DiJs.get<Navigator>() }
