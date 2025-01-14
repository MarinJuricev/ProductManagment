package database.path

private const val DATABASE_NAME = "MarinJuricevInventory"

private const val PARKING = "Parking"
private const val PARKING_REQUESTS = "Requests"
private const val GARAGE_LEVELS = "GarageLevels"
private const val TEMPLATES = "Templates"
const val PARKING_REQUEST_PATH = "$DATABASE_NAME/$PARKING/$PARKING_REQUESTS"
const val GARAGE_LEVELS_PATH = "$DATABASE_NAME/$PARKING/$GARAGE_LEVELS"
const val PARKING_TEMPLATES_PATH = "$DATABASE_NAME/$PARKING/$TEMPLATES"

private const val SEAT = "Seat"
private const val SEAT_REQUESTS = "Requests"
private const val SEAT_OFFICES = "Offices"
const val SEAT_RESERVATIONS_PATH = "$DATABASE_NAME/$SEAT/$SEAT_REQUESTS"
const val SEAT_OFFICES_PATH = "$DATABASE_NAME/$SEAT/$SEAT_OFFICES"

private const val USER = "User"
private const val USER_REGISTERED = "Registered"
const val REGISTERED_USERS_PATH = "$DATABASE_NAME/$USER/$USER_REGISTERED"
