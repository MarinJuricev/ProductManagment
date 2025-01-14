import ComposableArchitecture
import Dependencies
import Foundation
import Core

@Reducer
public struct MyReservationsFeature {
    
    @ObservableState
    public struct State: Equatable, Sendable {
        var parkingReservations: ParkingReservationsFeature.State
        // swiftlint:disable:next line_length
        var sheetHeightReading: CGFloat = 200 // NOTE: when set to .zero (which makes sense since we don't know the heigh), first reading of sheetHeight is sometimes kind of laggy. After setting it to 200 (it would never be less than 200), it works better
        @Presents var destination: Destination.State?

        var sheetHeight: CGFloat {
            sheetHeightReading + 10
        }

        public init(parkingReservations: ParkingReservationsFeature.State = ParkingReservationsFeature.State()) {
            self.parkingReservations = parkingReservations
        }
    }
  
    public enum Action: BindableAction, Sendable {
        case binding(BindingAction<State>)
        case parkingReservations(ParkingReservationsFeature.Action)
        case destination(PresentationAction<Destination.Action>)
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Scope(state: \.parkingReservations, action: \.parkingReservations) {
            ParkingReservationsFeature()
        }

        Reduce { state, action in
            switch action {
            case .parkingReservations(.delegate(.itemTapped(let parkingReservation))):
                state.sheetHeightReading = SheetEstimator.estimate(for: parkingReservation)
                state.destination = .details(MyReservationDetailsFeature.State(parkingReservation: parkingReservation))
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

extension MyReservationsFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case details(MyReservationDetailsFeature)
    }
}

extension MyReservationsFeature {
    enum SheetEstimator {
        static func estimate(for parkingReservation: IAParkingReservation) -> CGFloat {
            switch parkingReservation.status {
            case .declined, .cancelled:
                return 370
            case .approved:
                return 540
            case .submitted:
                return 440
            }
        }
    }
}
