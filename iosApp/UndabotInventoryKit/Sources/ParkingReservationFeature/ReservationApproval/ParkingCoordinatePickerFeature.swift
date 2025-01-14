import ComposableArchitecture
import Core
import Shared

@Reducer
public struct ParkingCoordinatePickerFeature {

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var garageLevels: [IAGarageLevelData]
        @Shared var parkingCoordinate: IAParkingCoordinate?

        var level: IAGarageLevel? {
            get {
                parkingCoordinate?.level
            }
            set {
                guard let newValue,
                      let spot = garageLevels.first(where: { $0.level.id == newValue.id })?.parkingSpots.first else { return }

                parkingCoordinate = IAParkingCoordinate(level: newValue, spot: spot)
            }
        }

        var spot: IAParkingSpot? {
            get {
                parkingCoordinate?.spot
            }
            set {
                guard let newValue,
                      let oldParkingCoordinate = parkingCoordinate else { return }
                
                parkingCoordinate = IAParkingCoordinate(level: oldParkingCoordinate.level, spot: newValue)
            }
        }

        public init(
            garageLevels: [IAGarageLevelData],
            parkingCoordinate: Shared<IAParkingCoordinate?>
        ) {
            self.garageLevels = garageLevels
            self._parkingCoordinate = parkingCoordinate
        }

        var garageLevelValues: [IAGarageLevel] {
            garageLevels.map { $0.level }
        }

        var garageSpotValues: [IAParkingSpot] {
            garageLevels.first { $0.level.id == parkingCoordinate?.level.id }.map { $0.parkingSpots.elements } ?? []
        }
    }

    public enum Action: BindableAction, Sendable {
        case binding(BindingAction<State>)
        case levelSelected(String?)
        case spotSelected(String?)
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        EmptyReducer()
    }
}
