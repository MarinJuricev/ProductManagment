import ComposableArchitecture
import Dependencies
import Shared
import Utilities

extension NewRequestFeature {
    public struct ErrorMapper: Sendable {
        public var map: @Sendable (Error) -> AlertState<NewRequestFeature.Action.Alert>
    }
}

extension NewRequestFeature.ErrorMapper: DependencyKey {
    public static let liveValue: NewRequestFeature.ErrorMapper = NewRequestFeature.ErrorMapper { error in
        guard let parkingReservationError = error as? ParkingReservationError else {
            return .unknownError
        }

        switch onEnum(of: parkingReservationError) {
        case .invalidEmailFormat:
            return .invalidEmailFormat
        case .onlyFutureDateReservationsAllowed:
            return .futureDateReservation
        case .unauthorized:
            return .unauthorized
        case .unknownError:
            return .unknownError
        case .duplicateReservation:
            return .duplicatedReservation
        case .lateReservation:
            return .lateReservation
        case .errorMessage(let error):
            return .message(error.message)
        }
    }

    public static let testValue: NewRequestFeature.ErrorMapper = NewRequestFeature.ErrorMapper { _ in
            .unknownError
    }
}

extension DependencyValues {
    public var newRequestErrorMapper: NewRequestFeature.ErrorMapper {
        get { self[NewRequestFeature.ErrorMapper.self] }
        set { self[NewRequestFeature.ErrorMapper.self] = newValue }
    }
}

public extension AlertState where Action == NewRequestFeature.Action.Alert {
    static let invalidEmailFormat = AlertState {
        TextState(resource: \.parking_reservation_new_request_invalid_email_error)
    }

    static let futureDateReservation = AlertState {
        TextState(resource: \.parking_reservation_date_validation_error)
    }

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

    static let duplicatedReservation = AlertState {
        TextState("You already have a parking request for this date")
    }

    static let lateReservation = AlertState {
        TextState("Parking reservations for tomorrow are not possible after 3:00 PM. Please contact us directly via facility@MarinJuricev.com or the #facility-support slack channel")
    }
}
