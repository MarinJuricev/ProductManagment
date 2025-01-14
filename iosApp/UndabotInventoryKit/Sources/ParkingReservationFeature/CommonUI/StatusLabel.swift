import SwiftUI
import Utilities
import Shared

public struct StatusLabel: View {
    private let title: String
    private let backgroundColor: Color

    public init(
        title: String,
        backgroundColor: Color
    ) {
        self.title = title
        self.backgroundColor = backgroundColor
    }

    public var body: some View {
        Text(title)
            .font(Font(resource: \.montserrat_semibold, size: 12))
            .foregroundStyle(Color(resource: \.surface))
            .frame(maxWidth: 90, maxHeight: 30)
            .background(backgroundColor)
            .containerShape(.rect(cornerRadius: 9))

    }
}
