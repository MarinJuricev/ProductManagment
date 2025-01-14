package org.product.inventory.web.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.toCssColor

@Composable
fun LoadingWrapper(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MR.colors.background.toCssColor()),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressBar()
        }
    } else {
        content()
    }
}
