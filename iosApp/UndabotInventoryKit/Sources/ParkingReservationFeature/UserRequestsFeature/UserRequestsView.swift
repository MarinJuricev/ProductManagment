import ComposableArchitecture
import SwiftUI
import Utilities
import Shared

public struct UserRequestsView: View {
    @Bindable public var store: StoreOf<UserRequestsFeature>

    public init(store: StoreOf<UserRequestsFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ParkingReservationsView(
                store: store.scope(state: \.parkingReservations, action: \.parkingReservations),
                itemView: { parkingReservation in
                    UserRequestItemView(with: parkingReservation)
                },
                emptyDataView: emptyDataView,
                filtersView: filtersView
            )
            .sheet(item: $store.scope(state: \.destination?.details, action: \.destination.details)) { detailsStore in
                WithPerceptionTracking {
                    UserRequestDetailsView(store: detailsStore)
                        .getHeight($store.sheetHeightReading)
                        .presentationDetents([.height(store.sheetHeight)])
                        .presentationDragIndicator(.visible)
                }
            }
        }
    }

    @ViewBuilder
    func emptyDataView() -> some View {
        VStack {
            Text(resource: \.parking_reservation_empty_state_error_title)
                .font(Font(resource: \.montserrat_semibold, size: 12))
            Text(resource: \.parking_reservation_empty_state_error_message)
                .font(Font(resource: \.montserrat_regular, size: 12))
        }
        .foregroundStyle(Color(resource: \.textBlack))
    }

    @ViewBuilder
    func filtersView() -> some View {
        HStack {
            HStack {
                TextField(MR.strings().user_request_garage_email_filter_search.desc().localized(), text: $store.searchText)
            }
            .padding(8)
            .cornerRadius(12)
            .overlay {
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(
                        style: StrokeStyle(lineWidth: 0.5)
                    )
                    .foregroundStyle(Color(resource: \.textLight))
            }

            Menu {
                 Picker("", selection: $store.statusFilter) {
                     ForEach(IAParkingReservationStatusType.allCases, id: \.self) { status in
                         Text(status.title).tag(status as IAParkingReservationStatusType?)
                     }
                     Text("All").tag(nil as IAParkingReservationStatusType?)
                 }
             } label: {
                 StatusPickerLabel(
                    title: store.statusFilter?.title ?? "All",
                    backgroundColor: store.statusFilter?.color ?? Color.gray,
                    spacing: 4,
                    horizontalPadding: 8,
                    verticalPadding: 4
                 )
             }
        }

    }
}
