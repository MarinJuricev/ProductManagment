import ComposableArchitecture
import SwiftUI
import CommonUI
import Core

@ViewAction(for: SeatReservationFeature.self)
public struct SeatReservationView: View {
    @Bindable public var store: StoreOf<SeatReservationFeature>

    public init(store: StoreOf<SeatReservationFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)

                VStack(spacing: 0) {
                    if let offices = store.officesData.loaded {
                        BorderedPickerView(values: offices.elements, selectedValue: $store.selectedOffice, horizontalPadding: 35)
                            .font(Font(resource: \.montserrat_semibold, size: 10))
                            .foregroundStyle(Color(resource: \.secondary))
                            .padding(.vertical, 19)
                    }
                    Group {
                        switch (store.officesData, store.reservableDatesData) {
                        case (.initial, .initial), (.initial, _), (_, .initial):
                            EmptyView()
                        case (.loading, _), (_, .loading):
                            ProgressView()
                                .progressViewStyle(.circular)
                        case (.loaded, .loaded):
                            if let store = store.scope(state: \.reservableDatesData.loaded, action: \.reservableDates) {
                                ReservableDatesView(store: store)
                            }

                        case (.failed, _):
                            Button(action: {
                                send(.retryOfficesButtonTapped)
                            }, label: {
                                Text(resource: \.general_retry_button_text)
                            })
                        case (_, .failed):
                            Button(action: {
                                send(.retryReservableDatesButtonTapped)
                            }, label: {
                                Text(resource: \.general_retry_button_text)
                            })
                        }
                    }
                    .frame(maxHeight: .infinity)
                }
            }
            .task {
                await send(.onTask).finish()
            }
        }
    }
}

extension IAOffice: PickableItem {}
