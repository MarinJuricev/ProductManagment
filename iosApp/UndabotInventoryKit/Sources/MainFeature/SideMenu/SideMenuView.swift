import SwiftUI
import Shared
import Utilities
import ComposableArchitecture

@ViewAction(for: SideMenuFeature.self)
struct SideMenuView: View {
    @Bindable var store: StoreOf<SideMenuFeature>

    let edgeTransition: AnyTransition = .move(edge: .leading)

    var body: some View {
        WithPerceptionTracking {
            ZStack(alignment: .bottom) {
                if store.isPresented {
                    background()
                    container()
                }
            }
            .animation(.easeInOut, value: store.isPresented)
        }
    }

    @ViewBuilder
    func background() -> some View {
        Color.black
            .opacity(0.5)
            .ignoresSafeArea()
            .onTapGesture {
                store.isPresented = false
            }
    }

    @ViewBuilder
    func container() -> some View {
        sideMenuView()
            .transition(edgeTransition)
    }

    @ViewBuilder
    func sideMenuView() -> some View {
        HStack {
            VStack(alignment: .leading, spacing: 0) {
                Image(resource: \.MarinJuricev_logo)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .padding(.horizontal, 69)
                    .padding(.bottom, 80)

                ForEach(store.features) { feature in
                    Button {
                        send(.featureSelected(feature), animation: .smooth(duration: 0.2))
                    } label: {
                        item(feature: feature)
                    }
                }
                Spacer()
            }
            .padding(.top, 32)
            .frame(width: 295)
            .background(
                Color(resource: \.surface)
            )
            Spacer()
        }
        .background(.clear)
    }

    @ViewBuilder
    func item(feature: SideMenuOption) -> some View {
        HStack(spacing: 18) {
            feature.icon
                .resizable()
                .renderingMode(.template)
                .aspectRatio(contentMode: .fit)
                .frame(width: 21, height: 21)
                .padding(.leading, feature == store.selectedFeature ? 15 : 10)
                .foregroundStyle(feature == store.selectedFeature ? Color(resource: \.surface) : Color(resource: \.textBlack))
            Text(feature.title)
                .foregroundStyle(feature == store.selectedFeature ? Color(resource: \.surface) : Color(resource: \.textBlack))
                .font(Font(resource: \.montserrat_semibold, size: 16))

            Spacer()
        }
        .frame(height: 58)
        .background(feature == store.selectedFeature ? Color(resource: \.secondary) : Color(resource: \.surface))
        .frame(maxWidth: .infinity)

    }
}
