import Shared
import Foundation
import CasePaths
import Core

@CasePathable
@dynamicMemberLookup
public enum IAReservableDate: Identifiable, Equatable, Sendable {
    case weekday(date: Date, seatHolders: [IAUser], remainingSeats: Int, isFullyReserved: Bool)
    case weekend(date: Date)

    public var id: Date {
        switch self {
        case .weekend(date: let date):
            return date
        case .weekday(date: let date, seatHolders: _, remainingSeats: _, isFullyReserved: _):
            return date
        }
    }

    public init(_ reservableDate: ReservableDate) {
        switch onEnum(of: reservableDate) {
        case .weekday(let weekday):
            // swiftlint:disable:next line_length
            self = .weekday(date: Date(milliseconds: weekday.date), seatHolders: weekday.seatHolders.map { IAUser($0) }, remainingSeats: Int(weekday.remainingSeats), isFullyReserved: weekday.isFullyReserved)
        case .weekend(let weekend):
            self = .weekend(date: Date(milliseconds: weekend.date))
        }
    }
}

extension Date {
    init(milliseconds: Int64) {
        self = Date(timeIntervalSince1970: TimeInterval(milliseconds) / 1000)
    }

    var millisecondsSince1970: Int64 {
        Int64((self.timeIntervalSince1970 * 1000.0).rounded())
    }
}
