package seatreservation.dashboard.model

import core.model.DashboardOption
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import org.product.inventory.shared.MR
import user.model.InventoryAppRole

sealed class SeatReservationOption(
    override val requiredRole: InventoryAppRole,
    override val icon: ImageResource,
    override val description: StringResource,
    override val title: StringResource,
) : DashboardOption(requiredRole, icon, description, title) {
    data object Timeline : SeatReservationOption(
        requiredRole = InventoryAppRole.User,
        title = MR.strings.seat_reservation_timeline_dashboard_option_title,
        description = MR.strings.seat_reservation_timeline_dashboard_option_description,
        icon = MR.images.timeline_icon,
    )
    data object SeatManagement : SeatReservationOption(
        requiredRole = InventoryAppRole.Administrator,
        title = MR.strings.seat_reservation_seat_management_dashboard_option_title,
        description = MR.strings.seat_reservation_seat_management_dashboard_option_description,
        icon = MR.images.seat_management_icon,
    )
}
