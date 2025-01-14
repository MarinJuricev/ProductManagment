import SwiftUI
import Shared
import Utilities

struct SplashView: View {
    var body: some View {
        Image(resource: \.MarinJuricev_logo)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .padding(.horizontal, 50)
    }
}
