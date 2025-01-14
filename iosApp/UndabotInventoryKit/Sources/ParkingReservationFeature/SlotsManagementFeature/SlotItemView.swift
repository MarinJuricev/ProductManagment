import ComposableArchitecture
import SwiftUI
import Shared

@ViewAction(for: SlotItemFeature.self)
public struct SlotItemView: View {
    public let store: StoreOf<SlotItemFeature>
    private let flexibleColumn = [
        GridItem(.flexible(minimum: 50, maximum: 100)),
        GridItem(.flexible(minimum: 50, maximum: 100)),
        GridItem(.flexible(minimum: 50, maximum: 100))
    ]

    private var levelShape: UnevenRoundedRectangle {
        .rect(topLeadingRadius: 23, bottomLeadingRadius: store.isExpanded ? 0 : 23, bottomTrailingRadius: store.isExpanded ? 23 : 0)
    }

    public var body: some View {
        WithPerceptionTracking {
            VStack {
                HStack {
                    ZStack {
                        Color(resource: \.secondary)
                            .clipShape(levelShape)
                        VStack {
                            Text(resource: \.slots_management_level_identifier)
                                .font(Font(resource: \.montserrat_regular, size: 12))
                            Text(store.garageLevelData.level.title)
                                .font(Font(resource: \.montserrat_bold, size: 20))
                        }
                        .foregroundStyle(Color.white)
                    }
                    .frame(width: 72)

                    HStack {
                        Text(resource: \.slots_management_number_of_spots_identifier)
                            .foregroundStyle(Color(resource: \.textBlack))
                        Text("\(store.garageLevelData.parkingSpots.count)")
                            .foregroundStyle(Color(resource: \.secondary))
                    }
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .frame(maxWidth: .infinity, alignment: .center)

                    Menu {
                        Button(MR.strings().slots_management_level_option_edit.desc().localized()) {
                            send(.editButtonTapped)
                        }
                        Button(MR.strings().slots_management_level_option_delete.desc().localized()) {
                            send(.deleteButtonTapped)
                        }
                    } label: {
                        Image(resource: \.dots)
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 30, height: 18)

                    }
                    .tint(Color(resource: \.textBlack))
                    .onTapGesture {}
                }
                .frame(height: 70)
                if store.isExpanded {
                    LazyVGrid(columns: flexibleColumn) {
                        ForEach(store.garageLevelData.parkingSpots) { parkingSpot in
                                HStack(spacing: 4) {
                                    Text(resource: \.slots_management_leading_spot_identifier_text)
                                    Text(parkingSpot.title)
                                }
                                .padding(.horizontal, 6)
                                .foregroundStyle(Color.white)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .frame(height: 25)
                                .background(Color(resource: \.secondary))
                                .clipShape(.rect(cornerRadius: 9))
                        }
                    }
                    .padding(.leading, 72)
                    .padding(.vertical, 26)
                }
            }
            .background(Color.white)
            .clipShape(.rect(cornerRadius: 23))
            .contentShape(.rect(cornerRadius: 23))
            .onTapGesture {
                send(.toggleExpansion, animation: .easeInOut)
            }
        }
    }
}
