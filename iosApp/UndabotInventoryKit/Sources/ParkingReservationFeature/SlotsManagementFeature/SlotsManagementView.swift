import ComposableArchitecture
import SwiftUI
import Shared
import Utilities

@ViewAction(for: SlotsManagementFeature.self)
public struct SlotsManagementView: View {
    public let store: StoreOf<SlotsManagementFeature>

    public init(store: StoreOf<SlotsManagementFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)
                VStack {
                    switch store.garageLevelData {
                    case .initial:
                        EmptyView()
                    case .loading:
                        ProgressView()
                            .progressViewStyle(.circular)
                    case .loaded:
                        if let store = store.scope(state: \.garageLevelData.loaded, action: \.garageLevelData) {
                            SlotsListView(store: store)
                        }

                    case .failed:
                        Button(action: {
                            send(.retryButtonTapped)
                        }, label: {
                            Text(resource: \.general_retry_button_text)
                        })
                    }
                }
            }
            .task {
                send(.onTask)
            }
        }
    }
}
