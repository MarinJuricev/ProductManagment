package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.utils.toLocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.product.inventory.shared.MR
import utils.clickable

@Composable
fun DatePickerField(
    displayedDate: String,
    selectedTimestamp: Long,
    onDateSelected: (timestamp: Long) -> Unit,
    confirmButtonText: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true,
    lowerDateLimit: Long = 0,
    upperDateLimit: Long = Long.MAX_VALUE,
) {
    var dialogShown by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = errorMessage?.let { colorResource(MR.colors.error.resourceId) }
                        ?: colorResource(MR.colors.textLight.resourceId),
                )
                .clickable(rippleEffectVisible = enabled) {
                    if (enabled) dialogShown = !dialogShown
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BodySmallText(
                text = displayedDate,
                fontWeight = FontWeight.Medium,
                color = MR.colors.textBlack,
            )
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(MR.images.calendar_icon.drawableResId),
                contentDescription = displayedDate,
            )
        }
        AnimatedVisibility(errorMessage != null) {
            BodySmallText(
                errorMessage.orEmpty(),
                color = MR.colors.error,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }

    if (dialogShown) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
                selectableDates = AvailableDates(lowerDateLimit, upperDateLimit),
            )
        datePickerState.selectedDateMillis = selectedTimestamp
        DatePickerDialog(
            colors = DatePickerDefaults.colors(containerColor = colorResource(MR.colors.surface.resourceId)),
            onDismissRequest = { dialogShown = false },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 80.dp)
                        .clickable(rippleEffectVisible = false) {
                            onDateSelected(datePickerState.selectedDateMillis ?: 0)
                            dialogShown = false
                        },
                ) {
                    BodyLargeText(
                        modifier = Modifier.align(Alignment.Center),
                        text = confirmButtonText,
                        color = MR.colors.textBlack,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
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

class AvailableDates(
    private val lowerDateLimit: Long,
    private val upperDateLimit: Long,
    private val forbiddenDates: List<Long> = emptyList(),
) :
    SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis in lowerDateLimit..upperDateLimit &&
            forbiddenDates.none { it.toLocalDate() == utcTimeMillis.toLocalDate() }

    override fun isSelectableYear(year: Int): Boolean =
        year >= Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
}
