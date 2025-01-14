package parking.dashboard

import arrow.core.Either
import arrow.core.raise.either
import auth.Authentication
import parking.dashboard.model.ParkingDashboardError
import parking.dashboard.model.ParkingDashboardError.ProfileNotFound
import parking.dashboard.model.ParkingDashboardOption
import parking.dashboard.model.ParkingDashboardOption.EmailTemplatesOption
import parking.dashboard.model.ParkingDashboardOption.MyReservationsOption
import parking.dashboard.model.ParkingDashboardOption.NewRequestOption
import parking.dashboard.model.ParkingDashboardOption.SlotsManagementOption
import parking.dashboard.model.ParkingDashboardOption.UserRequestsOption
import user.model.InventoryAppUser

class GetParkingDashboard(
    val authentication: Authentication,
) {
    suspend operator fun invoke(user: InventoryAppUser? = null): Either<ParkingDashboardError, List<ParkingDashboardOption>> =
        either {
            val selectedUser =
                user ?: authentication.getCurrentUser().mapLeft { ProfileNotFound }.bind()
            allOptions.filter { it.requiredRole <= selectedUser.role }
        }

    private val allOptions = listOf(
        NewRequestOption,
        UserRequestsOption,
        MyReservationsOption,
        EmailTemplatesOption,
        SlotsManagementOption,
    )
}
