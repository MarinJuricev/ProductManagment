package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.core.DateRange

data class UserRequestsHeaderData(
    val filterData: FilterData = FilterData(),
    val dateRange: DateRange = DateRange(),
)
