import Core
import CommonUI
import Foundation
import SwiftUI

extension IASeatReservationOption: IADashboardOption {
    public var optionTitle: String {
        switch self {
        case .timeline(let title, _, _):
            return title
        case .seatManagement(let title, _, _):
            return title
        }
    }

    public var optionDescription: String {
        switch self {
        case .timeline(_, let description, _):
            return description
        case .seatManagement(_, let description, _):
            return description
        }
    }

    public var optionIcon: Image? {
        switch self {
        case .timeline(_, _, let icon):
            if let uiImage = icon.toUIImage() {
                return Image(uiImage: uiImage)
            }
        case .seatManagement(_, _, let icon):
            if let uiImage = icon.toUIImage() {
                return Image(uiImage: uiImage)
            }
        }
        return nil
    }
}
