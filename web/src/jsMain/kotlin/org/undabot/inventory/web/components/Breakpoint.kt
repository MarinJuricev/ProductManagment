package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
fun rememberBreakpoint(): State<Breakpoint> {
    val breakpoint = remember { mutableStateOf(getCurrentBreakpoint()) }

    DisposableEffect(Unit) {
        val resizeListener: (Event) -> Unit = {
            breakpoint.value = getCurrentBreakpoint()
        }
        window.addEventListener("resize", resizeListener)
        onDispose {
            window.removeEventListener("resize", resizeListener)
        }
    }

    return breakpoint
}

// values retrieved from InitSilkContext: ctx.theme.breakpoints
fun getCurrentBreakpoint(): Breakpoint {
    val width = window.innerWidth
    return when {
        width >= 1280 -> Breakpoint.XL
        width >= 992 -> Breakpoint.LG
        width >= 768 -> Breakpoint.MD
        width >= 480 -> Breakpoint.SM
        else -> Breakpoint.ZERO
    }
}

operator fun Breakpoint.compareTo(other: Breakpoint): Int = when {
    this == other -> 0
    else -> ordinal.compareTo(other.ordinal)
}
