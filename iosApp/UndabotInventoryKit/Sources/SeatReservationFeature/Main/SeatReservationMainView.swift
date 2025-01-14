import ComposableArchitecture
import SwiftUI
import Core
import CommonUI
import Shared

@ViewAction(for: SeatReservationMainFeature.self)
public struct SeatReservationMainView: View {
    @Bindable public var store: StoreOf<SeatReservationMainFeature>

    public init(store: StoreOf<SeatReservationMainFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            VStack {
                switch store.screenType {
                case .initial:
                    EmptyView()
                case .loading:
                    ProgressView()
                        .progressViewStyle(.circular)
                case .loaded:
                    if let store = store.scope(state: \.screenType.loaded, action: \.screenType) {
                        switch store.case {
                        case .dashboard(let store):
                            DashboardView(store: store)
                        case .timeline(let store):
                            SeatReservationView(store: store)
                            
                        }
                    }
                case .failed:
                    Button(action: {
                        send(.onRetryButtonTapped)
                    }, label: {
                        Text(resource: \.general_retry_button_text)
                    })
                }
            }
            .frame(maxHeight: .infinity)
            .task {
                await send(.onTask).finish()
            }
            .navigationDestination(item: $store.scope(state: \.destination?.timeline, action: \.destination.timeline)) { store in
                SeatReservationView(store: store)
                    .navigationTitle("Timeline")
                    .navigationBarTitleDisplayMode(.inline)
            }
            .navigationDestination(item: $store.scope(state: \.destination?.seatManagement, action: \.destination.seatManagement)) { store in
                SeatManagementView(store: store)
                    .navigationTitle("Seat management")
                    .navigationBarTitleDisplayMode(.inline)
            }
        }
    }
}
