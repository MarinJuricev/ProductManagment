package parking.usersRequests.details.interaction

import kotlinx.datetime.Clock
import parking.reservation.model.GarageLevel
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationDetailsScreenTexts
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.reservation.model.ParkingReservationStatus.Canceled
import parking.reservation.model.ParkingReservationStatus.Declined
import parking.reservation.model.ParkingReservationStatus.Submitted
import parking.reservation.model.ParkingReservationStatusUiModel
import parking.reservation.model.ParkingReservationUiModel
import parking.reservation.model.ParkingSpot
import parking.usersRequests.details.model.GarageLevelUi
import parking.usersRequests.details.model.GarageSpotUi
import parking.usersRequests.details.model.HasGarageAccessUi

sealed interface ParkingReservationDetailsScreenState {
    data object Loading : ParkingReservationDetailsScreenState
    data class Content(
        val screenTexts: ParkingReservationDetailsScreenTexts,
        val currentStatus: ParkingReservationStatusUiModel,
        val availableStatusOptions: List<ParkingReservationStatusUiModel>,
        val garageLevelPickersVisible: Boolean,
        val adminNote: String,
        val adminNoteFormTitle: String,
        val selectedReservation: ParkingReservationUiModel,
        val adminNoteVisible: Boolean,
        val saveButtonVisible: Boolean,
        val saveButtonLoading: Boolean,
        val availableGarageLevels: List<GarageLevelUi>,
        val availableGarageSpots: List<GarageSpotUi>,
        val currentGarageLevel: GarageLevelUi,
        val currentGarageSpot: GarageSpotUi,
        val noSpaceLeftErrorVisible: Boolean,
        val garageAccessSwitchVisible: Boolean,
        val hasGarageAccessUi: HasGarageAccessUi,
    ) : ParkingReservationDetailsScreenState {
        fun toParkingReservation() = ParkingReservation(
            id = selectedReservation.id,
            email = selectedReservation.email,
            date = selectedReservation.dateTimeStamp,
            note = selectedReservation.note,
            status = when (currentStatus.status) {
                is Approved -> Approved(
                    adminNote = adminNote,
                    parkingCoordinate = ParkingCoordinate(
                        level = if (currentGarageLevel is GarageLevelUi.GarageLevelUiModel) {
                            currentGarageLevel.garageLevel
                        } else {
                            GarageLevel(
                                "",
                                "",
                            )
                        },
                        spot = if (currentGarageSpot is GarageSpotUi.GarageSpotUiModel) {
                            currentGarageSpot.spot
                        } else {
                            ParkingSpot(
                                id = "",
                                title = "",
                            )
                        },
                    ),
                )

                is Declined -> currentStatus.status.copy(adminNote = adminNote)
                is Canceled -> currentStatus.status
                is Submitted -> currentStatus.status
            },
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            createdAt = selectedReservation.createdAtTimeStamp,
        )
    }

    data object Error : ParkingReservationDetailsScreenState
}
