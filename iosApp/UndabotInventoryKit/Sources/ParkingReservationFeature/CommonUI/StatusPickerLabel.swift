import SwiftUI
import Utilities
import Shared

public struct StatusPickerLabel: View {
    private let title: String
    private let backgroundColor: Color
    private let spacing: CGFloat
    private let horizontalPadding: CGFloat
    private let verticalPadding: CGFloat

    init(
        title: String,
        backgroundColor: Color,
        spacing: CGFloat = 34,
        horizontalPadding: CGFloat = 16,
        verticalPadding: CGFloat = 9
    ) {
        self.title = title
        self.backgroundColor = backgroundColor
        self.spacing = spacing
        self.horizontalPadding = horizontalPadding
        self.verticalPadding = verticalPadding
    }

    public var body: some View {
        HStack(spacing: spacing) {
            StatusLabel(title: title, backgroundColor: backgroundColor)
            Image(systemName: "chevron.down.circle")
                .tint(Color(resource: \.textBlack))
        }
        .frame(height: 30)
        .padding(.horizontal, horizontalPadding)
        .padding(.vertical, verticalPadding)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .strokeBorder(
                    style: StrokeStyle(lineWidth: 0.5)
                )
                .foregroundColor(Color(resource: \.textLight))
        )
    }
}
