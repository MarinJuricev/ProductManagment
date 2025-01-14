package seat.management.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import components.BodyLargeText
import components.BodySmallText
import components.InventoryTextField
import org.product.inventory.shared.MR
import seatreservation.model.Office

@Composable
fun EditOfficeDialog(
    modifier: Modifier = Modifier,
    data: EditOfficeDialogData,
    officeNameChanged: (String) -> Unit,
    officeSeatsChanged: (String) -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(colorResource(MR.colors.surface.resourceId))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BodyLargeText(data.title, fontWeight = FontWeight.SemiBold)
            DialogHeader(
                officeNameLabel = data.officeNameLabel,
                seatsAmountLabel = data.seatsAmountLabel,
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InventoryTextField(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    value = data.office.title,
                    onValueChanged = officeNameChanged,
                    shape = RoundedCornerShape(12.dp),
                    textPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                )

                InventoryTextField(
                    modifier = Modifier.widthIn(max = 50.dp),
                    value = data.seatsAmount,
                    onValueChanged = officeSeatsChanged,
                    shape = RoundedCornerShape(12.dp),
                    textPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                )
            }
            DialogOptions(
                modifier = Modifier.padding(top = 16.dp),
                negativeOptionTitle = data.negativeOptionTitle,
                positiveOptionTitle = data.positiveOptionTitle,
                saveEnabled = data.saveEnabled,
                onCancelClick = onCancelClick,
                onSaveClick = onSaveClick,
            )
        }
    }
}

@Composable
private fun DialogHeader(
    officeNameLabel: String,
    seatsAmountLabel: String,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        BodySmallText(
            modifier = Modifier.fillMaxWidth(0.6f),
            text = officeNameLabel,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        BodySmallText(
            modifier = Modifier.width(50.dp),
            text = seatsAmountLabel,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
    }
}

@Composable
private fun DialogOptions(
    modifier: Modifier = Modifier,
    negativeOptionTitle: String,
    positiveOptionTitle: String,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    saveEnabled: Boolean,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BodySmallText(
            modifier = Modifier.padding(5.dp).clickable(onClick = onCancelClick),
            text = negativeOptionTitle,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
        BodySmallText(
            modifier = Modifier.padding(5.dp)
                .clickable(enabled = saveEnabled, onClick = onSaveClick)
                .alpha(if (saveEnabled) 1f else 0.5f),
            text = positiveOptionTitle,
            fontWeight = FontWeight.SemiBold,
            color = MR.colors.secondary,
        )
    }
}

data class EditOfficeDialogData(
    val title: String,
    val office: Office,
    val seatsAmount: String,
    val positiveOptionTitle: String,
    val negativeOptionTitle: String,
    val officeNameLabel: String,
    val seatsAmountLabel: String,
    val saveEnabled: Boolean,
)
