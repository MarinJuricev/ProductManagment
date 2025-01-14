package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import org.product.inventory.shared.MR
import parking.reservation.model.MultiDateSelectionState
import utils.clickable

@Composable
fun MultiDatePickerForm(
    modifier: Modifier = Modifier,
    uiState: MultiDateSelectionState,
    onDateSelected: (date: Long) -> Unit,
    onDateRemoved: (date: Long) -> Unit,
) {
    var dialogShown by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        BodySmallText(
            text = uiState.staticData.formTitle,
            color = MR.colors.secondary,
            fontWeight = SemiBold,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(visible = uiState.addingNewDateEnabled) {
                TextChip(
                    modifier = Modifier.padding(top = 17.dp),
                    text = uiState.staticData.addDateButtonText,
                    onClick = { dialogShown = true },
                )
            }
            uiState.selectedDates.forEach {
                DateChip(dateWithTimestamp = it, onRemoveClick = { onDateRemoved(it) })
            }
        }
        if (dialogShown) {
            val datePickerState =
                rememberDatePickerState(
                    initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
                    selectableDates = AvailableDates(
                        uiState.lowerDateLimit,
                        uiState.upperDateLimit,
                        uiState.forbiddenDates,
                    ),
                )
            datePickerState.selectedDateMillis = uiState.preselectedDate
            DatePickerDialog(
                colors = DatePickerDefaults.colors(containerColor = colorResource(MR.colors.surface.resourceId)),
                onDismissRequest = { dialogShown = false },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxHeight().widthIn(min = 80.dp)
                            .clickable(rippleEffectVisible = false) {
                                onDateSelected(datePickerState.selectedDateMillis ?: 0)
                                dialogShown = false
                            },
                    ) {
                        BodyLargeText(
                            modifier = Modifier.align(Alignment.Center),
                            text = uiState.staticData.datePickerConfirmSelectionText,
                            color = MR.colors.textBlack,
                            textAlign = TextAlign.Center,
                            fontWeight = SemiBold,
                        )
                    }
                },
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = colorResource(MR.colors.surface.resourceId),
                        selectedDayContainerColor = colorResource(MR.colors.secondary.resourceId),
                        todayDateBorderColor = colorResource(MR.colors.secondary.resourceId),
                        disabledDayContentColor = colorResource(MR.colors.textLight.resourceId),
                    ),
                )
            }
        }
    }
}
