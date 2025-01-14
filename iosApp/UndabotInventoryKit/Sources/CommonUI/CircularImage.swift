import SwiftUI
import Kingfisher
import Shared
import Utilities

public struct CircularImage: View {
    let url: URL?
    let diameter: CGFloat

    public init(
        url: URL?,
        diameter: CGFloat = 32
    ) {
        self.url = url
        self.diameter = diameter
    }

    public var body: some View {
        KFImage(url)
            .resizable()
            .frame(width: diameter, height: diameter)
            .clipShape(Circle())
            .contentShape(Circle())
            .overlay(Circle().stroke(Color(resource: \.secondary), lineWidth: 1))
    }
}
