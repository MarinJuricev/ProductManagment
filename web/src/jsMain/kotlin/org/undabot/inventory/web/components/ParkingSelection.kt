package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.px

val parkingSelectionStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.display(DisplayStyle.InlineBlock)
    }

    Breakpoint.SM {
        Modifier.display(DisplayStyle.Flex)
    }
}

@Composable
fun ParkingSelection(
    garageLevelsEntries: List<ParkingOption>,
    parkingSpotsEntries: List<ParkingOption>,
    garageLevelsEnabled: Boolean,
    parkingSpotsEnabled: Boolean,
    garageLevelDefault: ParkingOption,
    parkingSpotDefault: ParkingOption,
    garageLevelLabel: String,
    parkingSpotLabel: String,
    modifier: Modifier = Modifier,
    garageLevelChanged: (ParkingOption) -> Unit,
    parkingSpotChanged: (ParkingOption) -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(modifier = parkingSelectionStyle.toModifier()) {
            ReservationsAndRequestsLabelText(
                value = garageLevelLabel,
                modifier = Modifier
                    .margin(right = 20.px)
                    .align(Alignment.CenterStart),
            )

            ParkingOptionsDropdown(
                entries = garageLevelsEntries,
                enabled = garageLevelsEnabled,
                defaultItem = garageLevelDefault,
                onItemSelect = garageLevelChanged,
                modifier = Modifier.margin(right = 20.px),
            )
        }

        Box(modifier = parkingSelectionStyle.toModifier()) {
            ReservationsAndRequestsLabelText(
                value = parkingSpotLabel,
                modifier = Modifier
                    .margin(right = 20.px)
                    .align(Alignment.CenterStart),
            )

            ParkingOptionsDropdown(
                entries = parkingSpotsEntries,
                enabled = parkingSpotsEnabled,
                defaultItem = parkingSpotDefault,
                onItemSelect = parkingSpotChanged,
            )
        }
    }
}
