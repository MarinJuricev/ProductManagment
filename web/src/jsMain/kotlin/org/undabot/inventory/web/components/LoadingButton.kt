package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonSize
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import org.jetbrains.compose.web.attributes.ButtonType
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    type: ButtonType = ButtonType.Button,
    size: ButtonSize = ButtonSize.MD,
    content: @Composable RowScope.() -> Unit,
    loadingContent: @Composable RowScope.() -> Unit = { CircularProgressBar(color = Colors.White) },
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        colorScheme = colorScheme,
        type = type,
        size = size,
        enabled = enabled,
        content = {
            when (isLoading) {
                true -> loadingContent()
                false -> content()
            }
        },
        onClick = { onClick() },
    )
}
