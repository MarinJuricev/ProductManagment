import ComposableArchitecture
import Dependencies
import Shared
import Utilities

extension GarageLevelFormFeature {
    public struct ErrorMapper: Sendable {
        public var map: @Sendable (Error) -> AlertState<GarageLevelFormFeature.Action.Alert>
    }
}

extension GarageLevelFormFeature.ErrorMapper: DependencyKey {
    public static let liveValue: GarageLevelFormFeature.ErrorMapper = GarageLevelFormFeature.ErrorMapper { error in
        guard let parkingReservationError = error as? GarageLevelError else {
            return .genericError
        }

        switch onEnum(of: parkingReservationError) {
        case .unauthorized:
            return .unauthorized
        default:
            return .genericError
        }
    }

    public static let testValue: GarageLevelFormFeature.ErrorMapper = GarageLevelFormFeature.ErrorMapper { _ in
            .genericError
    }
}

extension DependencyValues {
    public var garageLevelFormErrormapper: GarageLevelFormFeature.ErrorMapper {
        get { self[GarageLevelFormFeature.ErrorMapper.self] }
        set { self[GarageLevelFormFeature.ErrorMapper.self] = newValue }
    }
}

public extension AlertState where Action == GarageLevelFormFeature.Action.Alert {
    static let genericError = AlertState {
        TextState(resource: \.general_unknown_error_title)
    }

    static let unauthorized = AlertState {
        TextState(resource: \.general_unauthorized_error)
    }
}
