package parking.reservation.model

data class ParkingReservationUiModel(
    val id: String,
    val email: String,
    val date: String,
    val dateTimeStamp: Long,
    val note: String,
    val adminNote: String,
    val reservationStatus: ParkingReservationStatusUiModel,
    val createdAt: String,
    val createdAtTimeStamp: Long,
    val updatedAt: String,
    val updatedAtTimeStamp: Long,
    val emailFieldName: String,
    val requestedDateFieldName: String,
    val submittedDateFieldName: String,
)

data class ParkingReservationStatusUiModel(
    val status: ParkingReservationStatus,
    val text: String,
)
