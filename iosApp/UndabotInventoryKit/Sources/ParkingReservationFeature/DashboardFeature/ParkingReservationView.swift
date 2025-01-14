import ComposableArchitecture
import SwiftUI
import Utilities
import Shared
import CommonUI

@ViewAction(for: ParkingReservationDashboardFeature.self)
public struct ParkingReservationView: View {
    @Bindable public var store: StoreOf<ParkingReservationDashboardFeature>

    public init(store: StoreOf<ParkingReservationDashboardFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            VStack {
                switch store.dashboard {
                case .initial:
                    EmptyView()
                case .loading:
                    ProgressView()
                        .progressViewStyle(.circular)
                case .loaded:
                    if let store = store.scope(state: \.dashboard.loaded, action: \.dashboard) {
                        DashboardView(store: store)
                    }
                case .failed:
                    Button(action: {
                        send(.retryButtonTapped)
                    }, label: {
                        Text(resource: \.general_retry_button_text)
                    })
                }
                VStack {}
                    .task {
                        await send(.onTask).finish()
                    }
                    .navigationDestination(item: $store.scope(state: \.destination?.newRequest, action: \.destination.newRequest)) { store in
                        NewRequestView(store: store)
                            .navigationTitle(MR.strings().parking_reservation_new_request_title.desc().localized())
                            .navigationBarTitleDisplayMode(.inline)
                    }
                    .navigationDestination(item: $store.scope(state: \.destination?.userRequests, action: \.destination.userRequests)) { store in
                        UserRequestsView(store: store)
                            .navigationTitle(MR.strings().parking_reservation_user_requests_title.desc().localized())
                            .navigationBarTitleDisplayMode(.inline)
                    }
                    .navigationDestination(item: $store.scope(state: \.destination?.myReservations, action: \.destination.myReservations)) { store in
                        MyReservationsView(store: store)
                            .navigationTitle(MR.strings().parking_reservation_my_reservations_title.desc().localized())
                            .navigationBarTitleDisplayMode(.inline)
                    }
            }
            .navigationDestination(item: $store.scope(state: \.destination?.slotsManagement, action: \.destination.slotsManagement)) { store in
                SlotsManagementView(store: store)
                    .navigationTitle(MR.strings().parking_reservation_slots_management_title.desc().localized())
                    .navigationBarTitleDisplayMode(.inline)
            }
            .navigationDestination(item: $store.scope(state: \.destination?.emailTemplates, action: \.destination.emailTemplates)) { store in
                EmailTemplatesView(store: store)
                    .navigationTitle("Email templates")
                    .navigationBarTitleDisplayMode(.inline)
            }
        }
    }
}
