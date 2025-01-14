package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import core.utils.millisNow
import core.utils.startOfTheDay
import core.utils.toLocalDate
import kotlinx.datetime.internal.JSJoda.DateTimeFormatter
import kotlinx.datetime.internal.JSJoda.LocalDate
import kotlinx.datetime.internal.JSJoda.ZoneId
import org.jetbrains.compose.web.dom.DateInput
import org.product.inventory.web.datetime.DATE_PATTERN
import org.product.inventory.web.datetime.ISO_DATE_PATTERN
import org.product.inventory.web.datetime.formatted
import org.product.inventory.web.datetime.millis
import org.product.inventory.web.datetime.toLocalDate

@Composable
fun DateInput(
    value: DateInputValue,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disableKeyboardInput: Boolean = true,
    minDate: DateInputValue? = null,
    maxDate: DateInputValue? = null,
    onValueChange: (DateInputValue) -> Unit,
) {
    DateInput(
        value = value.formatted,
        attrs = modifier
            .classNames("form-control")
            .enabledIf(enabled)
            .thenIf(
                condition = disableKeyboardInput,
                other = Modifier.onKeyDown { it.preventDefault() },
            )
            .toAttrs {
                minDate?.let { attr("min", it.formatted) }
                maxDate?.let { attr("max", it.formatted) }
                onInput { onValueChange(DateInputValue.fromString(it.value)) }
            },
    )
}

// format for DateInput component to satisfy the date input format from design
private val dateInputValueFormatter get() = DateTimeFormatter.ofPattern(ISO_DATE_PATTERN)

value class DateInputValue(val value: Long = millisNow()) {

    val formatted get() = value.toLocalDate(ZoneId.SYSTEM).format(dateInputValueFormatter)

    val formattedAsDayMonthYear
        get() = value.toLocalDate(ZoneId.SYSTEM).formatted(DateTimeFormatter.ofPattern(DATE_PATTERN))

    companion object {
        fun fromString(value: String): DateInputValue =
            LocalDate.parse(value, dateInputValueFormatter).toDateInputValue()
    }
}

fun Long.toDateInputValue() = DateInputValue(this)
fun LocalDate.toDateInputValue() = DateInputValue(millis)
fun DateInputValue.toLocalDate() = value.toLocalDate(ZoneId.SYSTEM)
fun DateInputValue.startOfTheDay(): DateInputValue = this.value.startOfTheDay().toLong().toDateInputValue()
