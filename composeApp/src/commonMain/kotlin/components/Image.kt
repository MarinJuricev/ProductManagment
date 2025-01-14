package components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import components.ImageType.Remote
import components.ImageType.Resource
import dev.icerock.moko.resources.ImageResource

@Composable
fun Image(
    imageType: ImageType,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    when (imageType) {
        is Remote -> AsyncImage(
            model = imageType.url,
            modifier = modifier,
            contentDescription = null,
            contentScale = contentScale,
        )

        is Resource -> Image(
            painter = painterResource(imageType.resource.drawableResId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}

sealed interface ImageType {
    data class Remote(val url: String) : ImageType
    data class Resource(val resource: ImageResource) : ImageType
}
