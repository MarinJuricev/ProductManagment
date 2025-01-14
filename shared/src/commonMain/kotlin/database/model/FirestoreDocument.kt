package database.model

sealed class FirestoreDocument(
    val identifier: String,
) {
    data object Parking : FirestoreDocument("Parking")
    data object Seat : FirestoreDocument("Seat")
    data object TestDevice : FirestoreDocument("Device")
    data object User : FirestoreDocument("User")
}
