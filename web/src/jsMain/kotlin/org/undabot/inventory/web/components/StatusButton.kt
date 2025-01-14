package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.components.forms.Button
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

@Composable
fun StatusButton(
    itemStatus: ParkingReservationStatusUi,
    modifier: Modifier = Modifier,
    textColor: Color.Rgb = Colors.White,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = { DefaultStatusButtonContent(itemStatus, textColor) },
) {
    Button(
        modifier = modifier,
        colorScheme = provideCustomColorScheme(itemStatus.color),
        content = { content() },
        onClick = { onClick() },
    )
}

@Composable
private fun DefaultStatusButtonContent(
    itemStatus: ParkingReservationStatusUi,
    textColor: Color.Rgb = Colors.White,
) {
    Text(
        value = itemStatus.text,
        modifier = Modifier.color(textColor),
    )
}
