import SwiftUI
import Utilities
import Shared

public struct PreviewDatePickerView: View {
    private var date: Date

    public init(date: Date) {
        self.date = date
    }

    public var body: some View {
        HStack {
            Text(date.format(using: .numericDottedFormatter))
                .font(Font(resource: \.montserrat_medium, size: 12))
                .foregroundStyle(Color(resource: \.textBlack))
            Spacer()
            Image(systemName: "calendar")
                .resizable()
                .frame(width: 18, height: 18)
                .foregroundStyle(Color(resource: \.textBlack))
        }
        .padding(8)
        .background(Color.white)
        .clipShape(.rect(cornerRadius: 8))
        .frame(maxWidth: .infinity)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .strokeBorder(
                    style: StrokeStyle(lineWidth: 0.5)
                )
                .foregroundColor(Color(resource: \.textLight))
        )
    }
}
