import SwiftUI

public struct PickableItemView: View {
    private let title: String
    private let isSelected: Bool

    public init(title: String, isSelected: Bool) {
        self.title = title
        self.isSelected = isSelected
    }

    public var body: some View {
        if isSelected {
            Label(title, systemImage: "checkmark")
        } else {
            Text(title)
        }
    }
}
