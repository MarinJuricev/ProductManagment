import ComposableArchitecture
import Dependencies
import Shared
import Utilities

extension CrewFormFeature {
    public struct ErrorMapper: Sendable {
        public var map: @Sendable (Error) -> AlertState<CrewFormFeature.Action.Alert>
    }
}

extension CrewFormFeature.ErrorMapper: DependencyKey {
    public static let liveValue: CrewFormFeature.ErrorMapper = CrewFormFeature.ErrorMapper { error in
        guard let createUserError = error as? CreateUserError else {
            return .unknownError
        }

        switch onEnum(of: createUserError) {
        case .createUserFailed:
            return .createUserFailed
        case .duplicatedUser:
            return .duplicatedUser
        case .invalidEmail:
            return .invalidEmail
        }
    }
}

extension DependencyValues {
    public var createUserErrorMapper: CrewFormFeature.ErrorMapper {
        get { self[CrewFormFeature.ErrorMapper.self] }
        set { self[CrewFormFeature.ErrorMapper.self] = newValue }
    }
}

public extension AlertState where Action == CrewFormFeature.Action.Alert {
    static let unknownError = AlertState {
        TextState(resource: \.general_unknown_error_title)
    }

    static let createUserFailed = AlertState {
        TextState(resource: \.crew_management_form_storage_error)
    }

    static let duplicatedUser = AlertState {
        TextState(resource: \.crew_management_form_duplicated_email)
    }

    static let invalidEmail = AlertState {
        TextState(resource: \.crew_management_form_invalid_email)
    }
}
