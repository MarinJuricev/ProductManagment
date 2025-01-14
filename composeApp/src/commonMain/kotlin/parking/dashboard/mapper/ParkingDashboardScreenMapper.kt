package parking.dashboard.mapper

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.dashboard.interaction.ParkingDashboardScreenState.Content
import parking.dashboard.interaction.ParkingDashboardScreenState.Retry
import parking.dashboard.model.ParkingDashboardError
import parking.dashboard.model.ParkingDashboardOption

class ParkingDashboardScreenMapper(
    private val dictionary: Dictionary,
) {
    operator fun invoke(dashboardResult: Either<ParkingDashboardError, List<ParkingDashboardOption>>) =
        when (dashboardResult) {
            is Left -> Retry(
                title = dictionary.getString(MR.strings.parking_reservation_retry_screen_title),
                description = dictionary.getString(MR.strings.parking_reservation_retry_screen_description),
                buttonText = dictionary.getString(MR.strings.parking_reservation_retry_screen_button_text),
            )

            is Right -> Content(items = dashboardResult.value)
        }
}
