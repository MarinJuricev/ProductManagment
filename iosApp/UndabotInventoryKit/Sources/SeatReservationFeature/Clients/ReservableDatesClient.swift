import Dependencies
import Core
import Utilities
import Shared
import Foundation

struct ReservableDatesClient {
    var observe: (_ for: String) throws -> AsyncThrowingStream<[IAReservableDate], Error>
    var reserveSeat: (_ officeId: String, _ date: Date) async throws -> Void
    var cancelReservation: (_ officeId: String, _ date: Date) async throws -> Void
}

extension ReservableDatesClient: DependencyKey {
    static let liveValue: ReservableDatesClient = ReservableDatesClient { officeId in
        @Dependency(\.date.now) var now

        let observeReservableDates: ObserveReservableDates = Di.shared.get()
        let dateConverter: DateConverter = Di.shared.get()
        let fromDate = now.millisecondsSince1970
        let timezone = dateConverter.formatTimeZone(platformTimeZone: TimeZone.current)
        let kotlinFlow = try SwiftEither(either: observeReservableDates.invoke(officeId: officeId, fromDate: fromDate, timezone: timezone)).get()
        let swiftFlow = SkieSwiftFlow<[ReservableDate]>(kotlinFlow)
        return AsyncThrowingStream(swiftFlow, mapper: { $0.map { IAReservableDate($0) }})
    } reserveSeat: { officeId, date in
        let reserve: ReserveSeat = Di.shared.get()
        _ = try await SwiftEither(either: reserve.invoke(officeId: officeId, date: date.millisecondsSince1970)).get()
    } cancelReservation: { officeId, date in
        let cancelReservation: CancelSeatReservation = Di.shared.get()
        _ = try await SwiftEither(either: cancelReservation.invoke(officeId: officeId, date: date.millisecondsSince1970)).get()
    }
}

extension DependencyValues {
    var reservableDatesClient: ReservableDatesClient {
        get { self[ReservableDatesClient.self] }
        set { self[ReservableDatesClient.self] = newValue }
    }
}
