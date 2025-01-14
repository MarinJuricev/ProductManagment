package parking.reservation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.BodyMediumText
import components.InventoryBigButton
import components.SubmittableDate
import org.product.inventory.shared.MR
import parking.reservation.model.MultiDateSelectionState

class SubmitRequestsStatusScreen(val uiState: MultiDateSelectionState) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Column(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp))
                .background(colorResource(MR.colors.surface.resourceId))
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BodyMediumText(
                    text = uiState.staticData.dateFormTitle,
                    color = MR.colors.secondary,
                    fontWeight = SemiBold,
                )

                BodyMediumText(
                    text = uiState.staticData.statusFormTitle,
                    color = MR.colors.secondary,
                    fontWeight = SemiBold,
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(
                    items = uiState.requestsForSubmission.toList(),
                    key = { it.first },
                ) {
                    SubmittableDate(state = it)
                }
            }

            AnimatedVisibility(uiState.closeSubmitStatusSheetButtonEnabled) {
                InventoryBigButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navigator.popUntilRoot() },
                ) {
                    BodyMediumText(
                        modifier = Modifier.padding(8.dp),
                        text = uiState.staticData.closeButtonText,
                        color = MR.colors.surface,
                        fontWeight = SemiBold,
                    )
                }
            }
        }
    }
}
