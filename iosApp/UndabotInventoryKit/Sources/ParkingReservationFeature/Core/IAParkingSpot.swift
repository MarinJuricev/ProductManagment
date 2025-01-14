import Shared

public struct IAParkingSpot: Identifiable, Equatable, Sendable {
    public var id: String
    public var title: String

    public init(id: String, title: String) {
        self.id = id
        self.title = title
    }

    public init(_ parkingSpot: ParkingSpot) {
        self.id = parkingSpot.id
        self.title = parkingSpot.title
    }
}

extension ParkingSpot {
    public convenience init(_ spot: IAParkingSpot) {
        self.init(
            id: spot.id,
            title: spot.title
        )
    }
}
