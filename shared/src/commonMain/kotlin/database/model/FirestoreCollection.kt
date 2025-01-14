package database.model

sealed class FirestoreCollection(
    val identifier: String,
) {
    data object ParkingRequests : FirestoreCollection("Requests")
    data object ParkingPlaces : FirestoreCollection("Places")
    data object RegisteredUsers : FirestoreCollection("Registered")
}
