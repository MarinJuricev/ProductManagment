import ComposableArchitecture
import SwiftUI

public struct ReservableDatesView: View {
    public let store: StoreOf<ReservableDatesFeature>

    public var body: some View {
        WithPerceptionTracking {
            ScrollView {
                VStack(spacing: 60) {
                    ForEach(store.scope(state: \.reservableDates, action: \.reservableDates)) { store in
                        ReservableDateView(store: store)
                    }
                }
                .padding(.vertical, 5)
            }
        }
    }
}
