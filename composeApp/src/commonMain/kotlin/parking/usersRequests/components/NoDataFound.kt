package parking.usersRequests.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.BodyLargeText

@Composable
fun NoDataFound(
    modifier: Modifier = Modifier,
    errorTitle: String,
    errorMessage: String,
) {
    Column(
        modifier = modifier.padding(vertical = 64.dp),
    ) {
        BodyLargeText(
            text = errorTitle,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier.height(8.dp))
        BodyLargeText(
            text = errorMessage,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
