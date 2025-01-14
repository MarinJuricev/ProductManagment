import ComposableArchitecture
import SwiftUI
import Core
import Utilities
import Shared

public struct DashboardView<Option: IADashboardOption>: View {
    public let store: StoreOf<DashboardFeature<Option>>

    public init(store: StoreOf<DashboardFeature<Option>>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ScrollView {
                VStack(spacing: 24) {
                    ForEach(store.options) { option in
                        OptionView(option: option) {
                            store.send(.optionTapped(option))
                        }
                    }
                }
                .padding(24)
            }
        }
    }
}
