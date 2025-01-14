import IdentifiedCollections
import Shared

public struct IAGarageLevelData: Identifiable, Equatable, Sendable {
    public var id: String
    public var level: IAGarageLevel
    public var parkingSpots: IdentifiedArrayOf<IAParkingSpot>

    public init(id: String, level: IAGarageLevel, parkingSpots: IdentifiedArrayOf<IAParkingSpot>) {
        self.id = id
        self.level = level
        self.parkingSpots = parkingSpots
    }

    public init(_ garageLevel: GarageLevelData) {
        self.id = garageLevel.id
        self.level = IAGarageLevel(garageLevel.level)
        self.parkingSpots = IdentifiedArray(uniqueElements: garageLevel.spots.map({ .init($0) }))
    }
}

extension GarageLevelData {
    public convenience init(_ garageLevelData: IAGarageLevelData) {
        self.init(
            id: garageLevelData.id,
            level: GarageLevel(garageLevelData.level),
            spots: garageLevelData.parkingSpots.map({ ParkingSpot($0) })
        )
    }
}

extension GarageLevelRequest {
    public convenience init(_ garageLevelData: IAGarageLevelData) {
        self.init(
            garageLevel: GarageLevel(garageLevelData.level),
            spots: garageLevelData.parkingSpots.map({ ParkingSpot($0) })
        )
    }
}
