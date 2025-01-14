import ComposableArchitecture
import SwiftUI
import Shared
import Utilities
import CommonUI

@ViewAction(for: GarageLevelFormFeature.self)
public struct GarageLevelFormView: View {
    @Bindable public var store: StoreOf<GarageLevelFormFeature>

    private let flexibleColumn = [
        GridItem(.flexible(minimum: 50, maximum: 100)),
        GridItem(.flexible(minimum: 50, maximum: 100)),
        GridItem(.flexible(minimum: 50, maximum: 100))
    ]

    public init(store: StoreOf<GarageLevelFormFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                VStack(alignment: .leading, spacing: 10) {
                    Text(resource: \.garage_level_creator_level_name_form_title)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    ValidatableTextField(store: store.scope(state: \.garageLevelTitleTextField, action: \.garageLevelTitleTextField))

                    ScrollViewReader { proxy in
                        if store.garageLevelData.parkingSpots.isEmpty {
                            Text(resource: \.garage_level_creator_spots_placeholder)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.textLight))
                                .frame(maxWidth: .infinity)
                        } else {
                            ScrollView {
                                LazyVGrid(columns: flexibleColumn) {
                                    ForEach(store.garageLevelData.parkingSpots) { parkingSpot in
                                        HStack(spacing: 4) {
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

                                            VStack {
                                                Button {
                                                    send(.deleteParkingSpot(parkingSpot.id))
                                                } label: {
                                                    Image(resource: \.close_round)
                                                        .tint(Color(resource: \.textLight))
                                                }
                                                Spacer()
                                            }
                                        }
                                        .id(parkingSpot.id)
                                        .frame(height: 38)
                                    }
                                }
                            }
                            .onChange(of: store.garageLevelData.parkingSpots) {
                                proxy.scrollTo(store.garageLevelData.parkingSpots.last?.id)
                            }
                        }
                    }
                    .frame(height: 175)
                    VStack(alignment: .leading) {
                        HStack(alignment: .top) {
                            ValidatableTextField(store: store.scope(state: \.newParkingSpotTitleTextField, action: \.newParkingSpotTitleTextField))

                            Button {
                                send(.addButtonTapped)
                            } label: {
                                Image(resource: \.plus)
                                    .resizable()
                                    .frame(width: 16, height: 16)

                            }
                            .frame(width: 36, height: 36)
                            .background(store.isAddParkingSpotButtonDisabled ? Color(resource: \.textLight) : Color(resource: \.secondary))
                            .clipShape(.circle)
                            .disabled(store.isAddParkingSpotButtonDisabled)
                        }
                    }

                }
                .padding()
                .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))

                if store.isRequestInFlight {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
            .task {
                await send(.onTask).finish()
            }
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(MR.strings().slots_management_save_button.desc().localized()) {
                        send(.saveButtonTapped)
                    }
                    .disabled(store.isSaveButtonDisabled)
                }
            }
        }
    }
}
