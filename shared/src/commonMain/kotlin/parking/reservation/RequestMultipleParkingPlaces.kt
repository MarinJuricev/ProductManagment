package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import parking.reservation.model.MultipleParkingRequestState
import parking.reservation.model.ParkingRequest
import parking.reservation.model.RequestMultipleParkingPlacesError
import parking.reservation.model.RequestMultipleParkingPlacesError.TooManyParkingRequests

class RequestMultipleParkingPlaces(
    private val manageParkingRequest: ManageParkingRequest,
) {

    operator fun invoke(
        requests: List<ParkingRequest>,
    ): Either<RequestMultipleParkingPlacesError, Flow<Map<Long, MultipleParkingRequestState>>> = either {
        ensure(requests.size <= PARKING_REQUESTS_LIMIT) { TooManyParkingRequests }

        channelFlow {
            val requestStatusMap = requests.associate { request ->
                request.date to MultipleParkingRequestState.LOADING
            }.toMutableMap()
            send(requestStatusMap)

            coroutineScope {
                requests.map { request ->
                    async {
                        val result = when (manageParkingRequest(request)) {
                            is Either.Left -> request.date to MultipleParkingRequestState.FAILURE
                            is Either.Right -> request.date to MultipleParkingRequestState.SUCCESS
                        }
                        requestStatusMap[result.first] = result.second
                        send(requestStatusMap.toMap())
                    }
                }.forEach { it.await() }
            }
        }
    }
}

const val PARKING_REQUESTS_LIMIT = 5
const val MY_RESERVATIONS_RANGE = 7
