import ComposableArchitecture
import SwiftUI
import Shared

@ViewAction(for: ReservationApprovalFeature.self)
public struct ReservationApprovalView: View {
    @Bindable public var store: StoreOf<ReservationApprovalFeature>

    public init(store: StoreOf<ReservationApprovalFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {

            ZStack {
                if store.shouldShowRetryButton {
                    retryButton
                } else if let parkingCoordinatePickerStore = store.scope(state: \.parkingCoordinatePickerFeature, action: \.parkingCoordinatePickerFeature) {
                    VStack {
                        ParkingCoordinatePickerView(store: parkingCoordinatePickerStore)
                        if store.shouldShowGarageAccessToggle {
                            Toggle(MR.strings().crew_management_form_garage_access.desc().localized(), isOn: $store.hasGarageAccess)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))
                        }
                    }
                    .disabled(store.isRequestInFlight)
                    .opacity(store.isRequestInFlight ? 0.5 : 1.0)
                } else if let emptyGarageLevels = store.garageLevelsData.loaded, emptyGarageLevels.isEmpty {
                    TakenParkingSpotsView()
                }

                if store.isRequestInFlight {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
            .frame(minHeight: 52)
            .task {
                await send(.onTask).finish()
            }
            .animation(.easeInOut, value: store.garageLevelsData)
            .animation(.easeInOut, value: store.userData)
        }
    }

    @ViewBuilder
    var retryButton: some View {
        Button {
            send(.onRetryButtonTapped)
        } label: {
            Text(resource: \.general_retry_button_text)
        }
    }
}
