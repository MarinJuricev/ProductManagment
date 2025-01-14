package seat.timeline.mapper

import seat.timeline.interaction.OfficeItem
import seat.timeline.interaction.OfficeItem.Loading
import seat.timeline.interaction.OfficeItem.OfficeData
import seat.timeline.interaction.ReservableDateUi
import seat.timeline.interaction.SeatReservationTimelineScreenState.Content
import seat.timeline.model.ReservableDateUiItem
import user.model.InventoryAppUser

class SeatReservationTimelineUiMapper(
    private val uiItemMapper: ReservableDateUiItemMapper,
) {
    fun map(
        availableOffices: List<OfficeItem>,
        selectedOffice: OfficeItem? = null,
        selectedUser: InventoryAppUser,
        reservableDates: ReservableDateUi,
    ): Content {
        val displayedSelectedOffice = calculateSelectedOffices(availableOffices, selectedOffice)
        return Content(
            selectedOffice = displayedSelectedOffice,
            availableOffices = availableOffices,
            reservableDateUi = generateReservableDateUi(
                reservableDates,
                selectedUser,
                selectedOffice,
            ),
        )
    }

    private fun generateReservableDateUi(
        reservableDates: ReservableDateUi,
        selectedUser: InventoryAppUser,
        selectedOffice: OfficeItem?,
    ) = when (reservableDates) {
        is ReservableDateUi.Content -> {
            when (selectedOffice) {
                is Loading -> listOf(ReservableDateUiItem.Loading)
                is OfficeData -> reservableDates.reservableDates.map { reservableDate ->
                    uiItemMapper.map(
                        reservableDate = reservableDate,
                        currentUser = selectedUser,
                        selectedOffice = selectedOffice.office,
                    )
                }

                else -> listOf(ReservableDateUiItem.Retry)
            }
        }

        is ReservableDateUi.Error -> listOf(ReservableDateUiItem.Retry)
        is ReservableDateUi.Loading -> listOf(ReservableDateUiItem.Loading)
    }

    private fun calculateSelectedOffices(
        availableOffices: List<OfficeItem>,
        selectedOffice: OfficeItem?,
    ): OfficeItem = when {
        selectedOffice != null -> selectedOffice
        availableOffices.any { it is OfficeData } -> availableOffices.first { it is OfficeData }
        else -> Loading
    }
}
