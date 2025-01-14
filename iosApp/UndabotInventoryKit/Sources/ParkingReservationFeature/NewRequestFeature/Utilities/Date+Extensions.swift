import Foundation

public extension Date {
    var millisecondsSince1970: Int64 {
        Int64((self.timeIntervalSince1970 * 1000.0).rounded())
    }

    init(milliseconds: Int64) {
        self = Date(timeIntervalSince1970: TimeInterval(milliseconds) / 1000)
    }

    func tomorrow(calendar: Calendar = Calendar.current) -> Date {
        guard let date = calendar.date(byAdding: .day, value: 1, to: self) else {
            fatalError("Unable to get end date from date")
        }
        return date
    }

    func add(calendar: Calendar = Calendar.current, days: Int) -> Date {
        guard let date = calendar.date(byAdding: .day, value: days, to: self) else {
            fatalError("Unable to get advanced date from date")
        }
        return date
    }

    func startOfDay(calendar: Calendar = Calendar.current) -> Date {
        calendar.startOfDay(for: self)
    }

    func startOfMonth(calendar: Calendar = Calendar.current) -> Date {
        guard let date = calendar.date(from: Calendar.current.dateComponents([.year, .month], from: self)) else {
            fatalError("Unable to get start date from date")
        }
        return date
    }

    func endOfMonth(calendar: Calendar = Calendar.current) -> Date {
        guard let date = calendar.date(byAdding: DateComponents(month: 1, day: -1), to: self.startOfMonth()) else {
            fatalError("Unable to get end date from date")
        }
        return date
    }
}
