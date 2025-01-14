import Shared

public struct IAParkingCoordinate: Equatable, Sendable {
    public var level: IAGarageLevel
    public var spot: IAParkingSpot

    public init(level: IAGarageLevel, spot: IAParkingSpot) {
        self.level = level
        self.spot = spot
    }

    public init?(_ parkingCoordinate: ParkingCoordinate?) {
        guard let parkingCoordinate else { return nil }
        self.init(
            level: IAGarageLevel(parkingCoordinate.level),
            spot: IAParkingSpot(parkingCoordinate.spot)
        )
    }

    public init(_ parkingCoordinate: ParkingCoordinate) {
        self.init(
            level: IAGarageLevel(parkingCoordinate.level),
            spot: IAParkingSpot(parkingCoordinate.spot)
        )
    }
}

extension ParkingCoordinate {
    public convenience init(_ parkingCoordinate: IAParkingCoordinate) {
        self.init(
            level: GarageLevel(parkingCoordinate.level),
            spot: ParkingSpot(parkingCoordinate.spot)
        )
    }
}
