import Foundation

extension DateFormatter {
    public static let numericDottedFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        return formatter
    }()
}

extension Date {
    public func format(using dateFormatter: DateFormatter) -> String {
        return dateFormatter.string(from: self)
    }
}
