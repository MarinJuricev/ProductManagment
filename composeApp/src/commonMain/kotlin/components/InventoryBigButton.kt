package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource
import org.product.inventory.shared.MR

@Composable
fun InventoryBigButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    backgroundColor: ColorResource = MR.colors.secondary,
    loadingIndicatorColor: ColorResource = MR.colors.surface,
    leadingIcon: ImageResource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(backgroundColor.resourceId),
            disabledBackgroundColor = colorResource(backgroundColor.resourceId).copy(alpha = 0.3f),
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = colorResource(loadingIndicatorColor.resourceId),
                    strokeWidth = 2.dp,
                )
            }
            leadingIcon?.let {
                AnimatedVisibility(!isLoading) {
                    Image(
                        modifier = Modifier.size(14.dp),
                        imageType = ImageType.Resource(leadingIcon),
                    )
                }
            }

            content()
        }
    }
}
