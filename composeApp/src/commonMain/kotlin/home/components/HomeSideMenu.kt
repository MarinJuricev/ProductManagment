package home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import components.BodyLargeText
import components.Image
import components.ImageType.Resource
import org.product.inventory.shared.MR

@Composable
fun HomeSideMenu(
    options: List<Tab>,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
) {
    val tabNavigator = LocalTabNavigator.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            imageType = Resource(MR.images.product_logo),
            modifier = Modifier.padding(horizontal = 69.dp, vertical = 32.dp),
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items(options) {
                MenuItem(
                    icon = it.options.icon,
                    text = it.options.title,
                    isSelected = tabNavigator.current == it,
                    onItemClick = {
                        tabNavigator.current = it
                        onItemClick()
                    },
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: Painter?,
    text: String,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .background(
                colorResource(if (isSelected) MR.colors.secondary.resourceId else MR.colors.surface.resourceId),
            )
            .padding(vertical = 19.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.size(21.dp),
                painter = it,
                contentDescription = text,
                tint = colorResource(if (isSelected) MR.colors.surface.resourceId else MR.colors.textBlack.resourceId),
            )
        }
        BodyLargeText(
            text,
            color = if (isSelected) MR.colors.surface else MR.colors.textBlack,
            fontWeight = SemiBold,
        )
    }
}
