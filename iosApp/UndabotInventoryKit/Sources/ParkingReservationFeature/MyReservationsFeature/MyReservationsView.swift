import ComposableArchitecture
import SwiftUI

public struct MyReservationsView: View {
    @Bindable public var store: StoreOf<MyReservationsFeature>

    public init(store: StoreOf<MyReservationsFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ParkingReservationsView(
                store: store.scope(state: \.parkingReservations, action: \.parkingReservations),
                itemView: { parkingReservation in
                    MyReservationItemView(with: parkingReservation)
                },
                emptyDataView: emptyDataView
            )
            .sheet(item: $store.scope(state: \.destination?.details, action: \.destination.details)) { detailsStore in
                WithPerceptionTracking {
                    MyReservationDetailsView(store: detailsStore)
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
}
