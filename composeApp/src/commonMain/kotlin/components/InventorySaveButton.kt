package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR

@Composable
fun InventorySaveButton(
    modifier: Modifier = Modifier,
    saveInProgress: Boolean,
    onSaveClick: () -> Unit,
) {
    val alpha: Float by animateFloatAsState(
        if (saveInProgress) 0.5f else 1f,
        label = "saveSaveButtonAlpha",
    )

    BadgedBox(
        content = {
            Image(
                modifier = modifier
                    .clip(CircleShape)
                    .graphicsLayer { this.alpha = alpha }
                    .clickable(enabled = !saveInProgress, onClick = onSaveClick),
                imageType = ImageType.Resource(MR.images.ic_save_template),
            )
        },
        badge = {
            AnimatedVisibility(saveInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(5.dp).size(10.dp),
                    color = colorResource(MR.colors.secondary.resourceId),
                    strokeWidth = 2.dp,
                )
            }
        },
    )
}
