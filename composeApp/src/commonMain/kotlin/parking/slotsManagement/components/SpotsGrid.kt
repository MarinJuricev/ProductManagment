package parking.slotsManagement.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BadgedBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.BodySmallText
import components.Image
import components.ImageType
import org.product.inventory.shared.MR
import parking.reservation.model.ParkingSpot
import utils.clickable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpotsGrid(
    spots: List<ParkingSpot>,
    spotIdentifier: String,
    modifier: Modifier = Modifier,
    removable: Boolean = false,
    onRemoveClick: (ParkingSpot) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var shouldScrollToTop by remember { mutableStateOf(true) }
    LaunchedEffect(spots.size) {
        if (shouldScrollToTop) {
            scrollState.animateScrollTo(0)
            shouldScrollToTop = false
        } else {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    FlowRow(
        modifier = modifier.verticalScroll(scrollState),
        horizontalArrangement = Arrangement.Center,
        maxItemsInEachRow = 3,
    ) {
        spots.forEach { spot ->
            ParkingSpot(
                removable = removable,
                title = spot.title,
                leadingSpotIdentifierText = spotIdentifier,
                onRemoveClick = { onRemoveClick(spot) },
            )
        }
    }
}

@Composable
private fun ParkingSpot(
    leadingSpotIdentifierText: String,
    title: String,
    removable: Boolean = false,
    onRemoveClick: () -> Unit = {},
) {
    BadgedBox(
        modifier = Modifier.padding(16.dp),
        content = {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(colorResource(MR.colors.secondary.resourceId))
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    BodySmallText(
                        text = leadingSpotIdentifierText,
                        color = MR.colors.surface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    BodySmallText(
                        text = title,
                        color = MR.colors.surface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        badge = {
            if (removable) {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .clip(CircleShape)
                        .clickable(rippleEffectVisible = false, onClick = onRemoveClick),
                    imageType = ImageType.Resource(MR.images.close_round),
                )
            }
        },
    )
}
