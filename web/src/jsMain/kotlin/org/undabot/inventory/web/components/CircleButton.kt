package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    radius: Int = 50,
    colorScheme: ColorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
    enabled: Boolean = true,
    content: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.size((radius * 2).px).clip(Circle(radius)),
        colorScheme = colorScheme,
        enabled = enabled,
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                content()
            }
        },
        onClick = { onClick() },
    )
}
