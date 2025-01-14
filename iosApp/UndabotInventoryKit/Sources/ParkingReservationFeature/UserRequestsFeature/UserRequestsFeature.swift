import Shared
import ComposableArchitecture
import Utilities
import Foundation
import Core

@Reducer
public struct UserRequestsFeature: Sendable {

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var parkingReservations: ParkingReservationsFeature.State
        // swiftlint:disable:next line_length
        var sheetHeightReading: CGFloat = 200 // NOTE: when set to .zero (which makes sense since we don't know the heigh), first reading of sheetHeight is sometimes kind of laggy. After setting it to 200 (it would never be less than 200), it works better
        @Presents var destination: Destination.State?
        @Shared var searchText: String
        @Shared var statusFilter: IAParkingReservationStatusType?

        var sheetHeight: CGFloat {
            sheetHeightReading + 10
        }

        public init() {
            let sharedSearchText = Shared("")
            let sharedStatusFilter: Shared<IAParkingReservationStatusType?> = Shared(nil)
            self.parkingReservations = ParkingReservationsFeature.State(searchText: sharedSearchText, statusFilter: sharedStatusFilter)
            self._searchText = sharedSearchText
            self._statusFilter = sharedStatusFilter
        }
    }

    public enum Action: BindableAction, Sendable {
        case binding(BindingAction<State>)
        case destination(PresentationAction<Destination.Action>)
        case parkingReservations(ParkingReservationsFeature.Action)
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Scope(state: \.parkingReservations, action: \.parkingReservations) {
            ParkingReservationsFeature()
                .transformDependency(\.parkingReservationsClient) { dependency in
                    dependency.observe = ParkingReservationsClient.userRequestsObservation
                }
        }

        Reduce { state, action in
            switch action {
            case .parkingReservations(.delegate(.itemTapped(let parkingReservation))):
                state.sheetHeightReading = SheetEstimator.estimate(for: parkingReservation)
                state.destination = .details(UserRequestDetailsFeature.State(parkingReservation: parkingReservation))
                return .none

            case .parkingReservations:
                return .none

            case .destination:
                return .none

            case .binding:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }
}

extension UserRequestsFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case details(UserRequestDetailsFeature)
    }
}

extension UserRequestsFeature {
    enum SheetEstimator {
        static func estimate(for parkingReservation: IAParkingReservation) -> CGFloat {
            switch parkingReservation.status {
            case .submitted, .cancelled:
                return 300
            case .approved, .declined:
                return 500
            }
        }
    }
}
