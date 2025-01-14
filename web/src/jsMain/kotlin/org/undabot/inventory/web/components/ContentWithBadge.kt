package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.translate
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px

@Composable
fun ContentWithBadge(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
    badgeContent: @Composable BoxScope.(Modifier) -> Unit,
) {
    Box(modifier = modifier) {
        content()

        badgeContent(
            Modifier
                .align(Alignment.TopEnd)
                .position(Position.Absolute)
                .translate(tx = 10.px, ty = (-10).px),
        )
    }
}
