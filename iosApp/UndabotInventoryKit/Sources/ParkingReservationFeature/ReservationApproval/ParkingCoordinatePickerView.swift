import ComposableArchitecture
import SwiftUI
import CommonUI
import Shared
import Utilities
import Core

public struct ParkingCoordinatePickerView: View {
    @Bindable public var store: StoreOf<ParkingCoordinatePickerFeature>

    public init(store: StoreOf<ParkingCoordinatePickerFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            HStack(spacing: 65) {
                VStack(alignment: .leading, spacing: 16) {
                    Text(resource: \.parking_reservation_item_garage_level)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    BorderedPickerView(
                        values: store.garageLevelValues,
                        selectedValue: $store.level
                    )
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.textBlack))
                }

                VStack(alignment: .leading, spacing: 16) {
                    Text(resource: \.parking_reservation_item_parking_spot)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    BorderedPickerView(values: store.garageSpotValues, selectedValue: $store.spot)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.textBlack))
                        .disabled(store.garageSpotValues.isEmpty)
                }
            }
        }
    }
}

extension IAGarageLevel: PickableItem {}
extension IAParkingSpot: PickableItem {}
