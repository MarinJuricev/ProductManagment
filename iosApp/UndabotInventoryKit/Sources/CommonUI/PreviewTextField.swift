import SwiftUI
import Utilities
import Shared

public struct PreviewTextField: View {
    let text: String

    public init(text: String) {
        self.text = text
    }

    public var body: some View {
        ScrollView {
            Text(text)
                .font(Font(resource: \.montserrat_regular, size: 12))
                .foregroundStyle(Color(resource: \.textBlack))
                .lineLimit(nil)
                .padding(6)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 100)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .strokeBorder(
                    style: StrokeStyle(lineWidth: 0.5)
                )
                .foregroundColor(Color(resource: \.textLight))
        )
        .scrollIndicators(.never)
    }
}
