package org.product.inventory.web.pages.newrequest

import core.utils.millisNow
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.ParkingOption

data class NewRequestEditableFields(
    val date: DateInputValue = DateInputValue(millisNow(dayOffset = 1)),
    val notes: String = "",
    val userListFilterText: String = "",
    val selectedGarageLevel: ParkingOption = ParkingOption.EMPTY,
    val selectedParkingSpot: ParkingOption = ParkingOption.EMPTY,
    val garageAccess: Boolean = false,
)
