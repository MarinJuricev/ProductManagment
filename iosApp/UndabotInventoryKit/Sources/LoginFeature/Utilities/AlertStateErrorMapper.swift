import ComposableArchitecture
import Shared
import Utilities

public struct AlertStateErrorMapper: Sendable {
    public var map: @Sendable (Error) -> AlertState<LoginFeature.Action.Alert>
}

extension AlertStateErrorMapper: DependencyKey {
    public static let liveValue: AlertStateErrorMapper = AlertStateErrorMapper { error in
        guard let authError = error as? AuthError else {
            return .genericError
        }

        switch onEnum(of: authError) {
        case .unknownAuthError:
            return .unknownAuthError
        case .credentialsNotReceived:
            return .credentialsNotReceived
        case .invalidCredentials:
            return .invalidCredentials
        case .unsupportedDomain:
            return .unsupportedDomain
        case .userNotFound:
            return .userNotFound
        case .storageError:
            return .storageError
        case .unauthorized:
            return .unauthorized
        case .invalidGoogleCredentials:
            return .unauthorized
        }
    }

    public static let testValue: AlertStateErrorMapper = AlertStateErrorMapper { _ in
            .unknownAuthError
//            .genericError
    }
}

extension DependencyValues {
    public var loginRequestErrorMapper: AlertStateErrorMapper {
        get { self[AlertStateErrorMapper.self] }
        set { self[AlertStateErrorMapper.self] = newValue }
    }
}

public extension AlertState where Action == LoginFeature.Action.Alert {
    static let genericError = AlertState {
        TextState(MR.strings().auth_message_error_title.desc().localized())
    }

    static let unknownAuthError = AlertState {
        TextState(resource: \.auth_message_error_title)
    } message: {
        TextState(resource: \.auth_message_error_description)
    }

    static let credentialsNotReceived = AlertState {
        TextState(resource: \.auth_message_error_title)
    }

    static let invalidCredentials = AlertState {
        TextState(resource: \.auth_message_invalid_credentials)
    }

    static let userNotFound = AlertState {
        TextState(resource: \.auth_message_error_title)
    }

    static let unsupportedDomain = AlertState {
        TextState(MR.strings().auth_message_warning_title.desc().localized())
    } message: {
        TextState(MR.strings().auth_message_warning_description.desc().localized())
    }

    static let storageError = AlertState {
        TextState(resource: \.auth_message_error_title)
    }

    static let unauthorized = AlertState {
        TextState(resource: \.general_unauthorized_error)
    }
}
