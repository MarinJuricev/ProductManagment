package seatreservation.dashboard

import arrow.core.Either
import arrow.core.raise.either
import auth.Authentication
import seatreservation.dashboard.model.SearReservationDashboardError
import seatreservation.dashboard.model.SeatReservationOption.SeatManagement
import seatreservation.dashboard.model.SeatReservationOption.Timeline
import user.model.InventoryAppRole
import user.model.InventoryAppUser

class GetSeatReservationDashboardType(
    val authentication: Authentication,
) {
    suspend operator fun invoke(user: InventoryAppUser? = null): Either<SearReservationDashboardError, SeatReservationDashboardType> =
        either {
            val selectedUser =
                user ?: authentication.getCurrentUser().mapLeft { SearReservationDashboardError.ProfileNotFound }.bind()

            when (selectedUser.role) {
                InventoryAppRole.User, InventoryAppRole.Manager -> SeatReservationDashboardType.User
                InventoryAppRole.Administrator -> SeatReservationDashboardType.Administrator(options = allOptions.filter { it.requiredRole <= selectedUser.role })
            }
        }

    private val allOptions = listOf(
        Timeline,
        SeatManagement,
    )
}
