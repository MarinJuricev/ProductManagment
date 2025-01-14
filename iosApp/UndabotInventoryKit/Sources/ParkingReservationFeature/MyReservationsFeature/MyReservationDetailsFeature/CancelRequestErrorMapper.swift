import ComposableArchitecture
import Dependencies
import Shared
import Utilities

extension MyReservationDetailsFeature {
    public struct ErrorMapper: Sendable {
        public var map: @Sendable (Error) -> AlertState<MyReservationDetailsFeature.Action.Alert>
    }
}

extension MyReservationDetailsFeature.ErrorMapper: DependencyKey {
    public static let liveValue: MyReservationDetailsFeature.ErrorMapper = MyReservationDetailsFeature.ErrorMapper { error in
        guard let parkingReservationError = error as? ParkingReservationError else {
            return .unknownError
        }

        switch onEnum(of: parkingReservationError) {
        case .unauthorized:
            return .unauthorized
        case .errorMessage(let error):
            return .message(error.message)
        default:
            return .unknownError
        }
    }

    public static let testValue: MyReservationDetailsFeature.ErrorMapper = MyReservationDetailsFeature.ErrorMapper { _ in
            .unknownError
    }
}

extension DependencyValues {
    public var cancelMyReservationErrorMapper: MyReservationDetailsFeature.ErrorMapper {
        get { self[MyReservationDetailsFeature.ErrorMapper.self] }
        set { self[MyReservationDetailsFeature.ErrorMapper.self] = newValue }
    }
}

public extension AlertState where Action == MyReservationDetailsFeature.Action.Alert {
    static let unauthorized = AlertState {
        TextState(resource: \.general_unauthorized_error)
    }

    static let unknownError = AlertState {
        TextState(resource: \.general_unknown_error_title)
    }

    static func message(_ message: String) -> AlertState {
        return AlertState {
            TextState(message)
        }
    }
}
