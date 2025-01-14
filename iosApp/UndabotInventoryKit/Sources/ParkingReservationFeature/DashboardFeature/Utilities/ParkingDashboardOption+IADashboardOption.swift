import Shared
import CommonUI
import SwiftUI

extension ParkingDashboardOption: @unchecked @retroactive Sendable {}
extension ParkingDashboardOption: @retroactive Identifiable {}
extension ParkingDashboardOption: IADashboardOption {
    public var optionTitle: String {
        title.desc().localized()
    }

    public var optionDescription: String {
        description_.desc().localized()
    }

    public var optionIcon: Image? {
        guard let uiImage = icon.toUIImage() else {
            return nil
        }
        return Image(uiImage: uiImage)
    }
}
