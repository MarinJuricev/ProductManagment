package org.product.inventory.web.pages.newrequest

import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes

class NewRequestStaticDataMapper(
    private val dictionary: Dictionary,
) {

    val staticData: NewRequestStaticData = NewRequestStaticData(
        breadcrumbItems = buildBreadcrumbItems(),
        title = dictionary.get(StringRes.newRequestTitle),
        personLabel = dictionary.get(StringRes.parkingReservationNewRequestAs),
        dateLabel = dictionary.get(StringRes.parkingReservationNewRequestDate),
        additionalNotesPlaceholder = dictionary.get(StringRes.parkingReservationNewRequestAdditionalNotesPlaceholder),
        submitText = dictionary.get(StringRes.parkingReservationNewRequestSubmit),
        userListFilterPlaceholder = dictionary.get(StringRes.parkingReservationNewRequestFilterPlaceholder),
        emptyUserListMessage = dictionary.get(StringRes.parkingReservationNewRequestEmptyUserListMessage),
        noParkingSpacesMessage = dictionary.get(StringRes.userRequestsNoParkingSpacesMessage),
    )

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.newRequestPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.newRequestPath2),
            route = Routes.parkingReservation,
            isNavigable = true,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.newRequestPath3),
            route = Routes.newRequest,
        ),
    )
}
