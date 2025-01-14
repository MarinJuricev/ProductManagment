package email

import core.utils.convertMillisToDate
import core.utils.getNameFromEmail
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingReservationStatus.Approved

class UpdateTemplateEmailWithData {
    operator fun invoke(
        template: String,
        parkingReservation: ParkingReservation,
        status: ParkingReservationStatus,
    ): String {
        val garageLevel = if (status is Approved) status.parkingCoordinate.level.title else ""
        val garageSpot = if (status is Approved) status.parkingCoordinate.spot.title else ""
        return template
            .replace(RECIPIENT_NAME, parkingReservation.email.getNameFromEmail())
            .replace(RESERVATION_NUMBER, parkingReservation.id)
            .replace(GARAGE_LEVEL, garageLevel)
            .replace(PARKING_SPOT, garageSpot)
            .replace(DATE, convertMillisToDate(parkingReservation.date))
    }
}

private const val RECIPIENT_NAME = "customer_name_here"
private const val RESERVATION_NUMBER = "reservation_number_here"
private const val GARAGE_LEVEL = "garage_level_here"
private const val PARKING_SPOT = "parking_spot_here"
private const val DATE = "check_in_date_here"
