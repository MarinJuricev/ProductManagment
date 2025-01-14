import ComposableArchitecture
import SwiftUI
import Shared
import CommonUI
import Core

@ViewAction(for: ParkingReservationsFeature.self)
public struct ParkingReservationsView<ItemView: View, EmptyDataView: View, SearchBar: View>: View {
    @Bindable public var store: StoreOf<ParkingReservationsFeature>
    @ViewBuilder let itemView: (IAParkingReservation) -> ItemView
    @ViewBuilder let emptyDataView: () -> EmptyDataView
    @ViewBuilder let filtersView: () -> SearchBar

    init(store: StoreOf<ParkingReservationsFeature>, 
         itemView: @escaping (IAParkingReservation) -> ItemView,
         emptyDataView: @escaping () -> EmptyDataView,
         filtersView: @escaping () -> SearchBar = { EmptyView() }
    ) {
        self.store = store
        self.itemView = itemView
        self.emptyDataView = emptyDataView
        self.filtersView = filtersView
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)
                VStack {
                    filters
                        .padding(.horizontal, 16)
                    Group {
                        switch store.data {
                        case .initial:
                            EmptyView()
                        case .loading:
                            ProgressView()
                                .progressViewStyle(.circular)
                        case .loaded:
                            if store.filteredData.isEmpty {
                                emptyDataView()
                            } else {
                                ScrollView {
                                    VStack(spacing: 19) {
                                        ForEach(store.filteredData) { parkingReservation in
                                            itemView(parkingReservation)
                                                .onTapGesture {
                                                    send(.itemTapped(parkingReservation))
                                                }
                                        }
                                    }
                                    .padding(.horizontal, 16)
                                }
                            }

                        case .failed:
                            Button(action: {
                                send(.retryButtonTapped)
                            }, label: {
                                Text(resource: \.general_retry_button_text)
                            })
                        }
                    }
                    .frame(maxHeight: .infinity)
                }
            }
            .task {
                send(.onTask)
            }
            .animation(.easeInOut, value: store.filteredData)
        }
    }

    @ViewBuilder
    var filters: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                VStack(alignment: .leading) {
                    Text(resource: \.parking_reservation_item_from)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    Button {
                        send(.fromDatePickerTapped, animation: .easeInOut)
                    } label: {
                        PreviewDatePickerView(date: store.startDate)
                    }
                    .datePicker(isPresented: $store.isFromDatePickerVisible, date: $store.startDate, range: .partialThrough(store.startDateRange))
                }
                VStack(alignment: .leading) {
                    Text(resource: \.parking_reservation_item_to)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    Button {
                        send(.toDatePickerTapped, animation: .easeInOut)
                    } label: {
                        PreviewDatePickerView(date: store.endDate)
                    }
                    .datePicker(isPresented: $store.isToDatePickerVisible, date: $store.endDate, range: .partialFrom(store.endDateRange))
                }
            }
            filtersView()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .background(Color.white)
        .clipShape(.rect(cornerRadius: 23))
    }
}
