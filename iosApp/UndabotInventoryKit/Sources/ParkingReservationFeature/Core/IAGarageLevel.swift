import Shared

public struct IAGarageLevel: Identifiable, Equatable, Sendable {
    public var id: String
    public var title: String

    public init(id: String, title: String) {
        self.id = id
        self.title = title
    }

    public init(_ garageLevel: GarageLevel) {
        self.id = garageLevel.id
        self.title = garageLevel.title
    }
}

extension GarageLevel {
    public convenience init(_ garageLevel: IAGarageLevel) {
        self.init(
            id: garageLevel.id,
            title: garageLevel.title
        )
    }
}
