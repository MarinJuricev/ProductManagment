import Shared
import SwiftNavigation
import SwiftUI

extension Text {
    public init(resource: KeyPath<MR.strings, Shared.StringResource>) {
        self.init(MR.strings()[keyPath: resource].desc().localized())
    }
}

extension Image {
    public init(resource: KeyPath<MR.images, Shared.ImageResource>) {
        self.init(uiImage: MR.images()[keyPath: resource].toUIImage()!)
    }
}

extension Color {
    public init(resource: KeyPath<MR.colors, Shared.ColorResource>) {
        self.init(uiColor: MR.colors()[keyPath: resource].getUIColor())
    }
}

extension Font {
    public init(resource: KeyPath<MR.fonts, Shared.FontResource>, size: Double) {
        self.init(MR.fonts()[keyPath: resource].uiFont(withSize: size))
    }
}

extension TextState {
    public init(resource: KeyPath<MR.strings, StringResource>) {
        self.init(MR.strings()[keyPath: resource].desc().localized())
    }
}
