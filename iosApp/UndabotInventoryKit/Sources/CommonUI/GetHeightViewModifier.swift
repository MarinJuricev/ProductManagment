import SwiftUI

public struct GetHeightViewModifier: ViewModifier {
    @Binding var height: CGFloat

    public func body(content: Content) -> some View {
        content.background(
            GeometryReader { geo -> Color in
                DispatchQueue.main.async {
                    if height != geo.size.height {
                        height = geo.size.height
                    }
                }
                return Color.clear
            }
        )
    }
}

extension View {
    public func getHeight(_ height: Binding<CGFloat>) -> some View {
        modifier(GetHeightViewModifier(height: height))
    }
}
