import ComposableArchitecture
import SwiftUI
import Shared

@ViewAction(for: SlotsListFeature.self)
public struct SlotsListView: View {
    @Bindable public var store: StoreOf<SlotsListFeature>

    public init(store: StoreOf<SlotsListFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                ScrollView {
                    VStack(spacing: 24) {
                        ForEach(store.scope(state: \.slots, action: \.slots)) { store in
                            SlotItemView(store: store)
                        }
                    }
                    .padding(24)
                }
                .disabled(store.isRequestInFlight)
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("", systemImage: "plus") {
                            send(.addButtonTapped)
                        }
                        .disabled(store.isRequestInFlight)
                        .tint(Color.black)
                    }
                }
                if store.isRequestInFlight {
                    Color.black.opacity(0.2)
                        .ignoresSafeArea()
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
            .sheet(item: $store.scope(state: \.destination?.editGarageLevel, action: \.destination.editGarageLevel)) { garageLevelFormStore in
                garageLevelCreator(store: garageLevelFormStore)
            }
            .sheet(item: $store.scope(state: \.destination?.addGarageLevel, action: \.destination.addGarageLevel)) { garageLevelFormStore in
                garageLevelCreator(store: garageLevelFormStore)
            }
            .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
        }
    }

    @ViewBuilder
    func garageLevelCreator(store: StoreOf<GarageLevelFormFeature>) -> some View {
        NavigationView {
            GarageLevelFormView(store: store)
                .navigationTitle("Level Creator")
                .navigationBarTitleDisplayMode(.inline)
                .interactiveDismissDisabled()
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button(MR.strings().slots_management_cancel_button.desc().localized()) {
                            send(.cancelButtonTapped)
                        }
                    }
                }
        }
        .presentationDetents([.height(400)])
    }
}
