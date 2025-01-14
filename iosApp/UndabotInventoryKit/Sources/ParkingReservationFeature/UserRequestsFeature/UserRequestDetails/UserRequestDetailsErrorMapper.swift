import ComposableArchitecture
import Dependencies
import Shared
import Utilities

extension UserRequestDetailsFeature {
    public struct ErrorMapper: Sendable {
        public var map: @Sendable (Error) -> AlertState<UserRequestDetailsFeature.Action.Alert>
    }
}

extension UserRequestDetailsFeature.ErrorMapper: DependencyKey {
    public static let liveValue: UserRequestDetailsFeature.ErrorMapper = UserRequestDetailsFeature.ErrorMapper { error in
        guard let userRequestDetailsError = error as? UserRequestsError else {
            return .unknownError
        }

        switch onEnum(of: userRequestDetailsError) {
        case .unauthorized:
            return .unauthorized
        case .unknownError:
            return .unknownError
        }
    }

    public static let testValue: UserRequestDetailsFeature.ErrorMapper = UserRequestDetailsFeature.ErrorMapper { _ in
            .unknownError
    }
}

extension DependencyValues {
    public var userRequestDetailsErrorMapper: UserRequestDetailsFeature.ErrorMapper {
        get { self[UserRequestDetailsFeature.ErrorMapper.self] }
        set { self[UserRequestDetailsFeature.ErrorMapper.self] = newValue }
    }
}

public extension AlertState where Action == UserRequestDetailsFeature.Action.Alert {
    static let unauthorized = AlertState {
        TextState(resource: \.general_unauthorized_error)
    }

    static let unknownError = AlertState {
        TextState(resource: \.general_unknown_error_title)
    }
}
