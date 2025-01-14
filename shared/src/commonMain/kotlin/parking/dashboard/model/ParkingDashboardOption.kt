package parking.dashboard.model

import core.model.DashboardOption
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import org.product.inventory.shared.MR
import user.model.InventoryAppRole

sealed class ParkingDashboardOption(
    override val requiredRole: InventoryAppRole,
    override val icon: ImageResource,
    override val description: StringResource,
    override val title: StringResource,
) : DashboardOption(requiredRole, icon, description, title) {
    data object NewRequestOption : ParkingDashboardOption(
        requiredRole = InventoryAppRole.User,
        title = MR.strings.parking_reservation_new_request_title,
        description = MR.strings.parking_reservation_new_request_description,
        icon = MR.images.new_request_icon,
    )

    data object UserRequestsOption : ParkingDashboardOption(
        requiredRole = InventoryAppRole.Manager,
        title = MR.strings.parking_reservation_user_requests_title,
        description = MR.strings.parking_reservation_user_requests_description,
        icon = MR.images.user_requests_icon,
    )

    data object MyReservationsOption : ParkingDashboardOption(
        requiredRole = InventoryAppRole.User,
        title = MR.strings.parking_reservation_my_reservations_title,
        description = MR.strings.parking_reservation_my_reservations_description,
        icon = MR.images.my_reservations_icon,
    )

    data object EmailTemplatesOption : ParkingDashboardOption(
        requiredRole = InventoryAppRole.Manager,
        title = MR.strings.parking_reservation_email_templates_title,
        description = MR.strings.parking_reservation_email_templates_description,
        icon = MR.images.email_templates_icon,
    )

    data object SlotsManagementOption : ParkingDashboardOption(
        requiredRole = InventoryAppRole.Administrator,
        title = MR.strings.parking_reservation_slots_management_title,
        description = MR.strings.parking_reservation_slots_management_description,
        icon = MR.images.parking_reservation_icon,
    )
}
