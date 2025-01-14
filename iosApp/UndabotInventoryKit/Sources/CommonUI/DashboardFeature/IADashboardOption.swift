import Shared
import SwiftUI

public protocol IADashboardOption: Identifiable, Equatable, Hashable, Sendable {
    var optionTitle: String { get }
    var optionDescription: String { get }
    var optionIcon: Image? { get }
}
