package parking.emailTemplates.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.BodySmallText
import org.product.inventory.shared.MR

@Composable
fun TemplateTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(MR.colors.surface.resourceId))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BodySmallText(stringResource(MR.strings.email_template_template_name.resourceId), fontWeight = FontWeight.SemiBold)
        BodySmallText(
            title,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
            textAlign = TextAlign.Center,
        )
    }
}
