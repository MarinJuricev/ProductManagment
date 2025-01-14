@preconcurrency import Shared
import CasePaths

@CasePathable
@dynamicMemberLookup
public enum IASeatReservationOption: Identifiable, Equatable, Sendable, Hashable {
    case timeline(title: String, description: String, icon: ImageResource)
    case seatManagement(title: String, description: String, icon: ImageResource)

    public var id: Self {
        self
    }

    init(_ seatReservationOption: SeatReservationOption) {
        switch onEnum(of: seatReservationOption) {
        case .seatManagement(let seatManagement):
            self = .seatManagement(title: seatManagement.title.desc().localized(), description: seatManagement.description_.desc().localized(), icon: seatManagement.icon)
        case .timeline(let timeline):
            self = .timeline(title: timeline.title.desc().localized(), description: timeline.description_.desc().localized(), icon: timeline.icon)
        }
    }
}
