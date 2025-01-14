package org.product.inventory.web.pages.newrequest

import core.utils.millisNow
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.models.ParkingSelectionData

data class NewRequestState(
    val profileInfo: ProfileInfo = ProfileInfo(),
    val staticData: NewRequestStaticData = NewRequestStaticData(),
    val date: DateInputValue = DateInputValue(millisNow(dayOffset = 1)),
    val minDate: DateInputValue = DateInputValue(millisNow(dayOffset = 1)),
    val notesLabel: String = "",
    val notesPlaceholder: String = "",
    val notes: String = "",
    val submitEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val alertMessage: AlertMessage? = null,
    val routeToNavigate: String? = null,
    val lateReservationMessage: String? = null,
    val requestMode: RequestMode = RequestMode.Basic(false),
    val userListFilterText: String = "",
    val closeDialog: Boolean = false,
    val garageAccess: Boolean = false,
    val garageLevelAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus? = null,
    val parkingSelectionData: ParkingSelectionData = buildDefaultParkingSelectionData(),
)

sealed interface RequestMode {
    data class Basic(val hasSelectedRequestDates: Boolean) : RequestMode
    data class Automatic(val userListItem: UserListItem) : RequestMode
}

data class NewRequestEvents(
    val closeDialog: Boolean = false,
    val isLoading: Boolean = false,
    val isLateReservation: Boolean = false,
    val routeToNavigate: String? = null,
)

data class NewRequestStaticData(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val personLabel: String = "",
    val dateLabel: String = "",
    val additionalNotesPlaceholder: String = "",
    val submitText: String = "",
    val userListFilterPlaceholder: String = "",
    val emptyUserListMessage: String = "",
    val noParkingSpacesMessage: String = "",
)

private fun buildDefaultParkingSelectionData() = ParkingSelectionData(
    garageLevelLabel = "",
    parkingSpotLabel = "",
    parkingErrorMessage = "",
    parkingErrorRetryMessage = "",
    noParkingSpacesMessage = "",
    garageLevels = emptyList(),
    parkingSpots = emptyList(),
    selectedGarageLevel = ParkingOption.EMPTY,
    selectedParkingSpot = ParkingOption.EMPTY,
    validParkingOption = false,
)
