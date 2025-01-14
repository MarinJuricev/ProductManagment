package parking.usersRequests.screenComponent.header.filter

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.usersRequests.screenComponent.header.datepicker.interaction.DateRangeData
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent.DateRangeChanged
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent.SearchEmailChanged
import parking.usersRequests.screenComponent.header.filter.interaction.FilterEvent.StatusChanged
import parking.usersRequests.screenComponent.header.filter.interaction.FilterViewEffect
import parking.usersRequests.screenComponent.header.filter.interaction.FilterViewEffect.DateRangeFilterApplied
import parking.usersRequests.screenComponent.header.filter.mapper.FilterStateUiMapper
import utils.convertMillisToDate

class FilterViewModel(
    uiMapper: FilterStateUiMapper,
) : ScreenModel {
    private val _filterState = MutableStateFlow(uiMapper.map())
    val filterState = _filterState.asStateFlow()

    private val _viewEffect = Channel<FilterViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<FilterViewEffect> = _viewEffect.receiveAsFlow()

    fun onEvent(event: FilterEvent) {
        when (event) {
            is DateRangeChanged -> {
                updateShownDate(event)
                notifyConsumers(event)
            }

            is SearchEmailChanged -> _filterState.update { it.copy(searchEmail = event.email) }
            is StatusChanged -> _filterState.update { it.copy(selectedStatus = event.status) }
        }
    }

    private fun notifyConsumers(event: DateRangeChanged) = screenModelScope.launch {
        _viewEffect.send(
            DateRangeFilterApplied(startDate = event.startDate, endDate = event.endDate),
        )
    }

    private fun updateShownDate(event: DateRangeChanged) = _filterState.update {
        it.copy(
            datePickerState = it.datePickerState.copy(
                dateRangeData = DateRangeData(
                    selectedStartDateTimestamp = event.startDate,
                    selectedEndDateTimestamp = event.endDate,
                    displayedStartDate = convertMillisToDate(event.startDate),
                    displayedEndDate = convertMillisToDate(event.endDate),
                    upperDateLimit = event.endDate,
                    loverDateLimit = event.startDate,
                ),
            ),
        )
    }
}
