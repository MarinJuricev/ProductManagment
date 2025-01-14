@preconcurrency import Shared
import Foundation

public struct DateResponse: Identifiable, Equatable, Sendable {
    let date: Date
    let state: MultipleParkingRequestState

    public var id: Date {
        date
    }
}
