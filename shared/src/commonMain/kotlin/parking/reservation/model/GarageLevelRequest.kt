package parking.reservation.model

data class GarageLevelRequest(
    val garageLevel: GarageLevel,
    val spots: List<ParkingSpot>,
) {
    fun toDto() = GarageLevelData(
        id = garageLevel.id,
        level = garageLevel.copy(title = garageLevel.title.trim()),
        spots = spots,
    )
}
