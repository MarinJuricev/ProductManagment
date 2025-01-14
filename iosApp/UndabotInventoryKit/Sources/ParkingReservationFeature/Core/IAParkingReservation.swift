import Shared
import Foundation
import Utilities

public struct IAParkingReservation: Identifiable, Equatable, Sendable {
    public var id: String
    public var email: String
    public var date: Date
    public var note: String
    public var status: IAParkingReservationStatus
    public var createdAt: Date
    public var updatedAt: Date

    public init(id: String, email: String, date: Date, note: String, status: IAParkingReservationStatus, createdAt: Date, updatedAt: Date) {
        self.id = id
        self.email = email
        self.date = date
        self.note = note
        self.status = status
        self.createdAt = createdAt
        self.updatedAt = updatedAt
    }

    public init(_ parkingReservation: ParkingReservation) {
        self.id = parkingReservation.id
        self.email = parkingReservation.email
        self.date = Date(milliseconds: parkingReservation.date)
        self.note = parkingReservation.note
        self.status = IAParkingReservationStatus(parkingReservation.status)
        self.createdAt = Date(milliseconds: parkingReservation.createdAt)
        self.updatedAt = Date(milliseconds: parkingReservation.updatedAt)
    }
}

extension ParkingReservation {
    public convenience init(_ parkingReservation: IAParkingReservation) {
        self.init(
            id: parkingReservation.id,
            email: parkingReservation.email,
            date: parkingReservation.date.millisecondsSince1970,
            note: parkingReservation.note,
            status: parkingReservation.status.toParkingReservationStatus()!,
            createdAt: parkingReservation.createdAt.millisecondsSince1970,
            updatedAt: parkingReservation.updatedAt.millisecondsSince1970
        )
    }
}
