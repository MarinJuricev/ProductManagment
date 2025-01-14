package parking.reservation

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.right
import arrow.fx.coroutines.parZip
import email.SendReservationUpdateEmail
import parking.reservation.model.GarageLevelData
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationStatus.Approved
import parking.reservation.model.UserRequestCancellationError
import parking.reservation.model.UserRequestCancellationError.GarageLevelDataFetchingFailed
import parking.reservation.model.UserRequestCancellationError.Unauthorized
import parking.templates.model.TemplateStatus.CanceledByAdmin
import parking.usersRequests.UpdateUserRequest
import parking.usersRequests.repository.UsersRequestsRepository

class UpdateInvalidUserRequests(
    private val cancelParkingPlaceRequest: CancelParkingPlaceRequest,
    private val updateUserRequest: UpdateUserRequest,
    private val getGarageLevels: GetGarageLevels,
    private val repository: UsersRequestsRepository,
    private val sendReservationUpdateEmail: SendReservationUpdateEmail,
) {
    suspend operator fun invoke(): Either<UserRequestCancellationError, Unit> =
        either {
            parZip(
                { getAllApprovedUserRequests() },
                { getGarageLevels() },
            ) { approvedRequests, garageLevelsData ->
                parZip(
                    {
                        updateParkingCoordinateIfNeeded(
                            approvedRequests = approvedRequests,
                            garageLevelsData = garageLevelsData,
                        )
                    },
                    {
                        cancelInvalidRequests(
                            approvedRequests = approvedRequests,
                            garageLevelsData = garageLevelsData,
                        )
                    },
                ) { _, sendEmailRequest ->
                    sendEmailRequest.right()
                }
            }
        }

    private suspend fun updateParkingCoordinateIfNeeded(
        approvedRequests: List<ParkingReservation>,
        garageLevelsData: List<GarageLevelData>,
    ) =
        approvedRequests
            .filterChangedTitleRequests(garageLevelsData = garageLevelsData)
            .forEach {
                updateUserRequest.invoke(parkingReservation = it)
            }

    private suspend fun cancelInvalidRequests(
        approvedRequests: List<ParkingReservation>,
        garageLevelsData: List<GarageLevelData>,
    ) =
        approvedRequests
            .filterNot { request ->
                garageLevelsData.contains((request.status as Approved).parkingCoordinate)
            }
            .forEach {
                cancelParkingPlaceRequest(parkingReservation = it)
                sendReservationUpdateEmail.invoke(reservation = it, templateStatus = CanceledByAdmin)
            }

    private fun List<ParkingReservation>.filterChangedTitleRequests(
        garageLevelsData: List<GarageLevelData>,
    ) = mapNotNull { request ->
        val approvedStatus = request.status as? Approved

        approvedStatus?.parkingCoordinate
            ?.getWithUpdatedTitle(garageLevelsData = garageLevelsData)
            ?.let { updatedParkingCoordinate ->
                request.copy(status = approvedStatus.copy(parkingCoordinate = updatedParkingCoordinate))
            }
    }

    private fun ParkingCoordinate.getWithUpdatedTitle(garageLevelsData: List<GarageLevelData>): ParkingCoordinate? =
        garageLevelsData
            .firstOrNull { it.level.id == level.id }
            ?.takeIf { it.spots.contains(spot) && it.level.title != level.title }
            ?.let { copy(level = it.level) }

    private fun List<GarageLevelData>.contains(parkingCoordinate: ParkingCoordinate): Boolean {
        val matchingGarageLevelData = this.firstOrNull { it.level.id == parkingCoordinate.level.id }
            ?: return false
        return matchingGarageLevelData.spots.contains(parkingCoordinate.spot)
    }

    private suspend fun Raise<UserRequestCancellationError>.getAllApprovedUserRequests(): List<ParkingReservation> =
        repository
            .getApprovedUsersRequests()
            .mapLeft { Unauthorized }
            .bind()

    private suspend fun Raise<UserRequestCancellationError>.getGarageLevels(): List<GarageLevelData> =
        getGarageLevels.invoke()
            .mapLeft { GarageLevelDataFetchingFailed }
            .bind()
}
