import Shared

public struct IAOffice: Identifiable, Equatable, Sendable {
    public var id: String
    public var title: String
    public var numberOfSeats: Int

    public init(id: String, title: String, numberOfSeats: Int) {
        self.id = id
        self.title = title
        self.numberOfSeats = numberOfSeats
    }

    public init(_ office: Office) {
        self.id = office.id
        self.title = office.title
        self.numberOfSeats = Int(office.numberOfSeats)
    }
}

extension Office {
    public convenience init(_ office: IAOffice) {
        self.init(id: office.id, title: office.title, numberOfSeats: Int32(office.numberOfSeats))
    }
}
