package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.product.inventory.shared.MR

@Composable
fun QuestionDialog(
    questionDialogData: QuestionDialogData,
    onNegativeActionClick: () -> Unit,
    onPositiveActionClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(22.dp))
                .background(colorResource(MR.colors.surface.resourceId))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            BodyLargeText(questionDialogData.title, fontWeight = SemiBold)
            BodyMediumText(questionDialogData.question, textAlign = TextAlign.Center)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BodyMediumText(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onNegativeActionClick)
                        .padding(8.dp),
                    text = questionDialogData.negativeActionText,
                    fontWeight = SemiBold,
                )
                BodyMediumText(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onPositiveActionClick)
                        .padding(8.dp),
                    text = questionDialogData.positiveActionText,
                    fontWeight = SemiBold,
                )
            }
        }
    }
}

data class QuestionDialogData(
    val isVisible: Boolean,
    val title: String,
    val question: String,
    val negativeActionText: String,
    val positiveActionText: String,
)
