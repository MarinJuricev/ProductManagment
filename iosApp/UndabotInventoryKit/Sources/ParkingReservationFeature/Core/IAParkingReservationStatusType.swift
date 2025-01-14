import Core
import CasePaths
import SwiftUI

@CasePathable
@dynamicMemberLookup
public enum IAParkingReservationStatusType: CaseIterable, Hashable, Sendable {
    case cancelled
    case submitted
    case approved
    case declined

    var title: String {
        self.toReservationStatus().title
    }

    var color: Color {
        self.toReservationStatus().color
    }

    init(_ parkingReservationStatus: IAParkingReservationStatus) {
        switch parkingReservationStatus {
        case .cancelled:
            self = .cancelled
        case .submitted:
            self = .submitted
        case .approved:
            self = .approved
        case .declined:
            self = .declined
        }
    }

    public static var userRequestCases: [IAParkingReservationStatusType] = [
        .submitted,
        .approved,
        .declined
    ]

    public static var requestsFilter: [IAParkingReservationStatusType] = [
        .submitted,
        .approved,
        .declined
    ]
}
