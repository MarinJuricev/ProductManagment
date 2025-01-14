import ComposableArchitecture
import SwiftUI
import Shared
import ParkingReservationFeature
import SeatReservationFeature
import CrewManagementFeature
import CommonUI
import Utilities

@ViewAction(for: MainFeature.self)
public struct MainView: View {
    @Bindable public var store: StoreOf<MainFeature>

    public init(store: StoreOf<MainFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            NavigationStack {
                ZStack {
                    Color(resource: \.background)
                        .ignoresSafeArea()
                    VStack(spacing: 0) {
                        HStack {
                            Menu {
                                Button(MR.strings().auth_logout_button_title.desc().localized()) {
                                    send(.logoutTapped)
                                }
                            } label: {
                                CircularImage(url: store.user.profileImageUrl)
                            }
                            VStack(alignment: .leading) {
                                Text(store.user.email.username)
                                    .font(Font(resource: \.montserrat_regular, size: 12))
                                    .foregroundStyle(Color(resource: \.textBlack))
                                Text(store.user.role.title)
                                    .font(Font(resource: \.montserrat_semibold, size: 12))
                                    .foregroundStyle(Color(resource: \.secondary))
                            }
                            Spacer()
                            Button {
                                send(.sideMenuButtonTapped)
                            } label: {
                                Image(resource: \.dots)
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(width: 30, height: 18)
                            }
                            .tint(Color(resource: \.textBlack))

                        }
                        .frame(height: 64)
                        .padding(.horizontal, 17)
                        .background(Color(resource: \.surface))

                        Group {
                            switch store.scope(state: \.selectedMenuFeature, action: \.selectedMenuFeature).case {
                            case .testDevices:
                                Text("TODO: Test devices feature")
                            case .parkingReservation(let parkingReservationStore):
                                ParkingReservationView(store: parkingReservationStore)
                            case .seatReservation(let seatReservationStore):
                                SeatReservationMainView(store: seatReservationStore)
                            case .crewManagement(let crewManagementStore):
                                CrewManagementView(store: crewManagementStore)

                            }
                        }
                        .frame(maxHeight: .infinity)
                    }

                    SideMenuView(store: store.scope(state: \.sideMenu, action: \.sideMenu))
                }
                .task {
                    await send(.onTask).finish()
                }
                .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
            }
        }
    }
}
