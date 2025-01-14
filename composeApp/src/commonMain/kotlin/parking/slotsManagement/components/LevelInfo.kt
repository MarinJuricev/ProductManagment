package parking.slotsManagement.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import components.BodySmallText
import org.product.inventory.shared.MR

@Composable
fun LevelInfo(
    levelIdentifier: String,
    level: String,
    isExpanded: Boolean = false,
) {
    val cornerRadius = 23
    val cornerSize by animateIntAsState(
        targetValue = if (isExpanded) cornerRadius else 0,
        label = level,
        animationSpec = tween(durationMillis = 300),
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
            .clip(
                RoundedCornerShape(
                    bottomEnd = cornerSize.dp,
                    bottomStart = (cornerRadius - cornerSize).dp,
                ),
            )
            .background(colorResource(MR.colors.secondary.resourceId))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BodySmallText(text = levelIdentifier, color = MR.colors.surface)
        BodySmallText(
            text = level,
            color = MR.colors.surface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = Ellipsis,
        )
    }
}
