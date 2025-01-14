import ComposableArchitecture
import Shared
import Foundation
import Core
import CommonUI
import Utilities

@Reducer
public struct GarageLevelFormFeature: Sendable {
    @Dependency(\.uuid) var uuid
    @Dependency(\.garageLevelFormErrormapper.map) var mapToAlertState
    @Dependency(\.dismiss) var dismiss
    private let validateGarageLevelTitle: @Sendable (IAGarageLevelData, String) -> String?
    private let validateParkingSpotTitle: @Sendable ([String], String) -> String?
    private let isGarageLevelTitleValid: @Sendable (IAGarageLevelData, String, [IAParkingSpot]) -> Bool
    private let isParkingSpotCreationValid: @Sendable ([String], String) -> Bool
    private let save: @Sendable (IAGarageLevelData) async throws -> Void

    public init(validateGarageLevelTitle: @Sendable @escaping (IAGarageLevelData, String) -> String?,
                validateParkingSpotTitle: @Sendable @escaping ([String], String) -> String?,
                isGarageLevelTitleValid: @Sendable @escaping (IAGarageLevelData, String, [IAParkingSpot]) -> Bool,
                isParkingSpotCreationValid: @Sendable @escaping ([String], String) -> Bool,
                save: @Sendable @escaping (IAGarageLevelData) async throws -> Void
                ) {
        self.validateGarageLevelTitle = validateGarageLevelTitle
        self.validateParkingSpotTitle = validateParkingSpotTitle
        self.isGarageLevelTitleValid = isGarageLevelTitleValid
        self.isParkingSpotCreationValid = isParkingSpotCreationValid
        self.save = save
    }

    @ObservableState
    public struct State: Equatable, Sendable {
        @Presents var destination: Destination.State?
        var initialGarageLevelData: IAGarageLevelData
        @Shared var garageLevelData: IAGarageLevelData
        @Shared var newParkingSpotTitle: String
        var garageLevelTitleTextField: ValidatableTextFieldFeature.State
        var newParkingSpotTitleTextField: ValidatableTextFieldFeature.State
        var isRequestInFlight: Bool
        var successAlertMessage: String
        var isSaveButtonDisabled: Bool = true
        var isAddParkingSpotButtonDisabled: Bool = true

        public init(garageLevelData: IAGarageLevelData,
                    isRequestInFlight: Bool = false,
                    newParkingSpotTitle: String = "",
                    destination: Destination.State? = nil,
                    successAlertMessage: String
        ) {
            let sharedNewParkingSpotTitle = Shared(newParkingSpotTitle)
            let sharedGarageLevelData = Shared(garageLevelData)
            self.initialGarageLevelData = garageLevelData
            self._garageLevelData = sharedGarageLevelData
            self._newParkingSpotTitle = sharedNewParkingSpotTitle
            // swiftlint:disable line_length
            self.newParkingSpotTitleTextField = ValidatableTextFieldFeature.State(text: sharedNewParkingSpotTitle, placeholder: MR.strings().garage_level_creator_spot_name_placeholder.desc().localized())
            self.garageLevelTitleTextField = ValidatableTextFieldFeature.State(text: sharedGarageLevelData.level.title, placeholder: MR.strings().garage_level_creator_level_name_placeholder.desc().localized())
            // swiftlint:enable line_length
            self.isRequestInFlight = isRequestInFlight
            self.destination = destination
            self.successAlertMessage = successAlertMessage
        }

        var trimmedNewParkingSpotTitle: String {
            newParkingSpotTitle.trimmingCharacters(in: .whitespacesAndNewlines)
        }

        var trimmedGarageLevelTitle: String {
            garageLevelData.level.title.trimmingCharacters(in: .whitespacesAndNewlines)
        }
    }

    public enum Action: BindableAction, ViewAction, Sendable {
        case binding(BindingAction<State>)
        case view(ViewAction)
        case saveGarageLevelResponse(Result<Void, Error>)
        case destination(PresentationAction<Destination.Action>)
        case newParkingSpotTitleTextField(ValidatableTextFieldFeature.Action)
        case garageLevelTitleTextField(ValidatableTextFieldFeature.Action)
        case garageLevelTitleChanged(String)
        case parkingSpotTitleChanged(String)

        public enum Alert: Equatable, Sendable {
            case successConfirmation
        }

        @CasePathable
        public enum ViewAction: Sendable {
            case onTask
            case saveButtonTapped
            case cancelButtonTapped
            case addButtonTapped
            case deleteParkingSpot(IAParkingSpot.ID)
        }
    }

    public var body: some ReducerOf<Self> {
        ReducerReader { state, _ in
            Scope(state: \.garageLevelTitleTextField, action: \.garageLevelTitleTextField) {
                ValidatableTextFieldFeature { text in
                    return validateGarageLevelTitle(state.initialGarageLevelData, text)
                }
            }

            Scope(state: \.newParkingSpotTitleTextField, action: \.newParkingSpotTitleTextField) {
                ValidatableTextFieldFeature { text in
                    let unavailableTitles = state.garageLevelData.parkingSpots.map { $0.title }
                    return validateParkingSpotTitle(unavailableTitles, text)
                }
            }
        }

        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return .merge(
                    .publisher { state.$newParkingSpotTitle.publisher.map(Action.parkingSpotTitleChanged) },
                    .publisher { state.$garageLevelData.level.title.publisher.map(Action.garageLevelTitleChanged) }
                )
            case .view(.addButtonTapped):
                let parkingSpot = IAParkingSpot(id: uuid().uuidString, title: state.trimmedNewParkingSpotTitle)
                state.garageLevelData.parkingSpots.append(parkingSpot)
                state.newParkingSpotTitleTextField.reset()
                return .none

            case .view(.deleteParkingSpot(let id)):
                state.garageLevelData.parkingSpots.remove(id: id)
                return .none
            case .view(.saveButtonTapped):
                return saveGarageLevelData(state: &state)

            case .view(.cancelButtonTapped):
                return .run { _ in
                    await dismiss()
                }

            case .saveGarageLevelResponse(.success):
                state.isRequestInFlight = false
                state.destination = .alert(.success(message: state.successAlertMessage))
                return .none

            case .saveGarageLevelResponse(.failure(let error)):
                state.isRequestInFlight = false
                state.destination = .alert(mapToAlertState(error))
                return .none

            case .destination(.presented(.alert(.successConfirmation))):
                return .run { _ in
                    await dismiss()
                }

            case .garageLevelTitleChanged:
                state.isSaveButtonDisabled = !isGarageLevelTitleValid(
                    state.initialGarageLevelData,
                    state.garageLevelData.level.title,
                    state.garageLevelData.parkingSpots.elements
                )
                return .none

            case .parkingSpotTitleChanged:
                let unavailableTitles = state.garageLevelData.parkingSpots.map { $0.title }
                state.isAddParkingSpotButtonDisabled = !isParkingSpotCreationValid(
                    unavailableTitles,
                    state.newParkingSpotTitle
                )
                return .none

            case .newParkingSpotTitleTextField:
                return .none

            case .garageLevelTitleTextField:
                return .none

            case .binding:
                return .none

            case .destination:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func saveGarageLevelData(state: inout State) -> EffectOf<Self> {
        state.isRequestInFlight = true
        state.garageLevelData.level.title = state.trimmedGarageLevelTitle
        return .run { [garageLevel = state.garageLevelData] send in
            await send(.saveGarageLevelResponse(Result { try await save(garageLevel) }))
        }
    }
}

extension GarageLevelFormFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        @ReducerCaseEphemeral
        case alert(AlertState<GarageLevelFormFeature.Action.Alert>)
    }
}

extension AlertState where Action == GarageLevelFormFeature.Action.Alert {
    static func success(message: String) -> AlertState {
        return AlertState {
            TextState(message)
        } actions: {
            ButtonState(action: .successConfirmation) {
                TextState(resource: \.general_ok)
            }
        }
    }
}
