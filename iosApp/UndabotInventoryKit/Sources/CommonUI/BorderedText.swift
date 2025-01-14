import SwiftUI
import Utilities
import Shared

public struct BorderedText: View {
    private let text: String

    public init(_ text: String) {
        self.text = text
    }

    public var body: some View {
        Text(text)
            .frame(minWidth: 90, minHeight: 35)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(
                        style: StrokeStyle(lineWidth: 0.5)
                    )
                    .foregroundColor(Color(resource: \.textLight))
            )
    }
}
