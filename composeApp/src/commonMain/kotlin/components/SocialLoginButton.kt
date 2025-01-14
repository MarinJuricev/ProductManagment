package components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import components.ImageType.Resource
import dev.icerock.moko.resources.ImageResource
import org.product.inventory.TextBlackColor

@Composable
fun SocialLoginButton(
    modifier: Modifier = Modifier,
    providerIcon: ImageResource,
    text: String,
    providerName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .border(width = 2.dp, color = TextBlackColor, shape = RoundedCornerShape(13.dp))
            .padding(horizontal = 8.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageType = Resource(resource = providerIcon),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(38.dp),
        )
        BodySmallText(text, modifier = Modifier.padding(end = 2.dp))
        BodySmallText(providerName, fontWeight = SemiBold)
    }
}
