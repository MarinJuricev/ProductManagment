package parking.usersRequests.screenComponent.header.datepicker

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.usersRequests.mapper.DateRangePickerUiMapper
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangeData
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerEvent
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerEvent.DateRangeChanged
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerViewEffect
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangePickerViewEffect.ValidationSucceeded
import utils.ValidateDateRange
import utils.convertMillisToDate

class DateRangePickerViewModel(
    private val pickerUiMapper: DateRangePickerUiMapper,
    private val dateRangeValidation: ValidateDateRange,
) : ScreenModel {
    private val _pickerState = MutableStateFlow(pickerUiMapper.map())
    val pickerState = _pickerState.asStateFlow()

    private val _viewEffect = Channel<DateRangePickerViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<DateRangePickerViewEffect> = _viewEffect.receiveAsFlow()

    fun onEvent(event: DateRangePickerEvent) {
        when (event) {
            is DateRangeChanged -> {
                updateShownDate(event)
                when (val validationResult = dateRangeValidation(event.startDate, event.endDate)) {
                    is Either.Left -> {
                        _pickerState.update {
                            it.copy(
                                startDateError = validationResult.value.startDateError,
                                endDateError = validationResult.value.endDateError,
                            )
                        }
                    }

                    is Either.Right -> handleValidDate(
                        event.startDate,
                        event.endDate,
                    )
                }
            }
        }
    }

    private fun updateShownDate(event: DateRangeChanged) = _pickerState.update {
        it.copy(
            dateRangeData = DateRangeData(
                selectedStartDateTimestamp = event.startDate,
                selectedEndDateTimestamp = event.endDate,
                displayedStartDate = convertMillisToDate(event.startDate),
                displayedEndDate = convertMillisToDate(event.endDate),
                upperDateLimit = event.endDate,
                loverDateLimit = event.startDate,
            ),
        )
    }

    private fun handleValidDate(
        startDate: Long,
        endDate: Long,
    ) = screenModelScope.launch {
        _pickerState.update {
            it.copy(
                startDateError = null,
                endDateError = null,
            )
        }
        _viewEffect.send(
            ValidationSucceeded(
                startDate = startDate,
                endDate = endDate,
            ),
        )
    }
}
