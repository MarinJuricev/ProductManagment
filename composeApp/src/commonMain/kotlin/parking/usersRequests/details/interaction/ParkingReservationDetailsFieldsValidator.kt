package parking.usersRequests.details.interaction

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.usersRequests.details.model.GarageSpotUi

class ParkingReservationDetailsFieldsValidator(
    val dictionary: Dictionary,
) {
    operator fun invoke(
        data: ParkingReservationDetailsScreenState.Content,
    ): Either<String, Unit> = if (data.currentStatus.status is Approved && data.currentGarageSpot !is GarageSpotUi.GarageSpotUiModel) {
        dictionary.getString(MR.strings.parking_reservation_missing_parking_spot_data).left()
    } else {
        Unit.right()
    }
}
