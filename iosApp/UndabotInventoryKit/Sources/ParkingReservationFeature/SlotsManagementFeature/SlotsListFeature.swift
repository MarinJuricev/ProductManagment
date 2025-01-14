import ComposableArchitecture
import Core
import Shared
import Utilities

@Reducer
public struct SlotsListFeature {

    @Dependency(\.garageLevelsClient) var garageLevelsClient
    @Dependency(\.garageLevelValidator) var garageLevelValidator

    @ObservableState
    public struct State: Equatable, Sendable {
        var slots: IdentifiedArrayOf<SlotItemFeature.State>
        var isRequestInFlight: Bool
        @Presents var destination: Destination.State?

        public init(
            slots: IdentifiedArrayOf<SlotItemFeature.State>,
            isRequestInFlight: Bool = false,
            destination: Destination.State? = nil
        ) {
            self.slots = slots
            self.isRequestInFlight = isRequestInFlight
            self.destination = destination
        }
    }

    public enum Action: Sendable, ViewAction {
        case slots(IdentifiedActionOf<SlotItemFeature>)
        case deleteGarageLevelResponse(Result<Void, Error>)
        case destination(PresentationAction<Destination.Action>)
        case view(View)

        public enum View: Sendable {
            case addButtonTapped
            case cancelButtonTapped
        }

        public enum Alert: Sendable, Equatable {
            case deleteConfirmation(SlotItemFeature.State.ID)
            case successfullDeletionConfirmation
        }
    }

    enum CancelID: Hashable {
        case deleteSlot(SlotItemFeature.State.ID)
    }

    public var body: some ReducerOf<Self> {
        ReducerReader { state, _ in
            Reduce { state, action in
                switch action {
                case .slots(.element(id: let id, action: .delegate(.deleteButtonTapped))):
                    state.destination = .alert(.deleteConfirmation(with: id))
                    return .none

                case .slots(.element(id: let id, action: .delegate(.editButtonTapped))):
                    guard let garageLevel = state.slots[id: id]?.garageLevelData else { return .none }
                    // swiftlint:disable:next line_length
                    state.destination = .editGarageLevel(GarageLevelFormFeature.State(garageLevelData: garageLevel, successAlertMessage: MR.strings().slots_management_request_edit_success.desc().localized()))
                    return .none

                case .view(.addButtonTapped):
                    @Dependency(\.uuid) var uuid
                    let id = uuid().uuidString

                    let emptyGarageLevel = IAGarageLevelData(id: id, level: IAGarageLevel(id: id, title: ""), parkingSpots: [])
                    // swiftlint:disable:next line_length
                    state.destination = .addGarageLevel(GarageLevelFormFeature.State(garageLevelData: emptyGarageLevel, successAlertMessage: MR.strings().slots_management_request_add_success.desc().localized()))
                    return .none

                case .view(.cancelButtonTapped):
                    state.destination = nil
                    return .none

                case .deleteGarageLevelResponse(.success):
                    state.isRequestInFlight = false
                    state.destination = .alert(.successfullDeletionConfirmation)
                    return .none

                case .deleteGarageLevelResponse(.failure):
                    state.isRequestInFlight = false
                    state.destination = .alert(.failedDeletionConfirmation)
                    return .none
                case .slots:
                    return .none

                case .destination(.presented(.alert(.successfullDeletionConfirmation))):
                    state.destination = nil
                    return .none

                case .destination(.presented(.alert(.deleteConfirmation(let id)))):
                    return deleteGarageLevel(state: &state, with: id)

                case .destination:
                    return .none
                }
            }
            .ifLet(\.$destination, action: \.destination) {
                Destination { garageLevelData, title in
                    let unavailableGarageLevels = state.slots.map { $0.garageLevelData.level }
                    return garageLevelValidator.validateEditGarageLevelTitle(
                        initialGarageLevel: garageLevelData.level,
                        unavailableGarageLevels: unavailableGarageLevels,
                        title: title
                    )
                } validateAddGarageLevelTitle: { title in
                    let unavailableGarageLevelTitles = state.slots.map { $0.garageLevelData.level.title }
                    return garageLevelValidator.validateAddGarageLevelTitle(
                        unavailableGarageLevelTitles: unavailableGarageLevelTitles,
                        title: title
                    )
                } validateParkingSpotTitle: { unavailableTitles, title in
                    garageLevelValidator.validateParkingSpotTitle(
                        unavailableGarageLevelTitles: unavailableTitles,
                        title: title
                    )
                } isGarageLevelEditingValid: { garageLevelData, title, spots in
                    let unavailableGarageLevels = state.slots.map { $0.garageLevelData.level }
                    return garageLevelValidator.isGarageLevelEditingValid(
                        initialGarageLevelData: garageLevelData,
                        unavailableGarageLevels: unavailableGarageLevels,
                        title: title,
                        spots: spots
                    )
                } isGarageLevelCreationValid: { title, spots in
                    let unavailableGarageLevelTitles = state.slots.map { $0.garageLevelData.level.title }
                    return garageLevelValidator.isGarageLevelCreationValid(
                        unavailableGarageLevelTitles: unavailableGarageLevelTitles,
                        title: title,
                        spots: spots
                    )
                } isParkingSpotCreationValid: { unavailableTitles, title in
                    garageLevelValidator.isParkingSpotCreationValid(
                        unavailableTitles: unavailableTitles,
                        title: title
                    )
                }
            }
            .forEach(\.slots, action: \.slots) {
                SlotItemFeature()
            }
        }
    }

    private func deleteGarageLevel(state: inout State, with id: SlotItemFeature.State.ID) -> EffectOf<Self> {
        state.isRequestInFlight = true
        return .run { send in
            await send(.deleteGarageLevelResponse(Result { try await garageLevelsClient.deleteLevel(with: id) }))
        }
        .cancellable(id: CancelID.deleteSlot(id))
    }
}

extension SlotsListFeature {
    @Reducer
    public struct Destination {
        private let validateEditGarageLevelTitle: @Sendable (IAGarageLevelData, String) -> String?
        private let validateAddGarageLevelTitle: @Sendable (String) -> String?
        private let validateParkingSpotTitle: @Sendable ([String], String) -> String?
        private let isGarageLevelEditingValid: @Sendable (IAGarageLevelData, String, [IAParkingSpot]) -> Bool
        private let isGarageLevelCreationValid: @Sendable (String, [IAParkingSpot]) -> Bool
        private let isParkingSpotCreationValid: @Sendable ([String], String) -> Bool

        public init(
            validateEditGarageLevelTitle: @Sendable @escaping (IAGarageLevelData, String) -> String?,
            validateAddGarageLevelTitle: @Sendable @escaping (String) -> String?,
            validateParkingSpotTitle: @Sendable @escaping ([String], String) -> String?,
            isGarageLevelEditingValid: @Sendable @escaping (IAGarageLevelData, String, [IAParkingSpot]) -> Bool,
            isGarageLevelCreationValid: @Sendable @escaping (String, [IAParkingSpot]) -> Bool,
            isParkingSpotCreationValid: @Sendable @escaping ([String], String) -> Bool
        ) {
            self.validateEditGarageLevelTitle = validateEditGarageLevelTitle
            self.validateAddGarageLevelTitle = validateAddGarageLevelTitle
            self.validateParkingSpotTitle = validateParkingSpotTitle
            self.isGarageLevelEditingValid = isGarageLevelEditingValid
            self.isGarageLevelCreationValid = isGarageLevelCreationValid
            self.isParkingSpotCreationValid = isParkingSpotCreationValid
        }

        @ObservableState
        @dynamicMemberLookup
        public enum State: Equatable, Sendable {
            case editGarageLevel(GarageLevelFormFeature.State)
            case addGarageLevel(GarageLevelFormFeature.State)
            case alert(AlertState<SlotsListFeature.Action.Alert>)
        }

        public enum Action: Sendable {
            case editGarageLevel(GarageLevelFormFeature.Action)
            case addGarageLevel(GarageLevelFormFeature.Action)
            case alert(SlotsListFeature.Action.Alert)
        }

        public var body: some ReducerOf<Self> {
            Scope(state: \.editGarageLevel, action: \.editGarageLevel) {
                GarageLevelFormFeature(
                    validateGarageLevelTitle: validateEditGarageLevelTitle,
                    validateParkingSpotTitle: validateParkingSpotTitle,
                    isGarageLevelTitleValid: isGarageLevelEditingValid,
                    isParkingSpotCreationValid: isParkingSpotCreationValid
                ) { garageLevel in
                    let updateGarageLevel: UpdateGarageLevel = Di.shared.get()
                    let either = try await updateGarageLevel.invoke(garageLevelData: GarageLevelData(garageLevel))
                    _ = try SwiftEither(either: either).get()
                }
            }

            Scope(state: \.addGarageLevel, action: \.addGarageLevel) {
                GarageLevelFormFeature(
                    validateGarageLevelTitle: { _, title in
                        validateAddGarageLevelTitle(title)
                    },
                    validateParkingSpotTitle: validateParkingSpotTitle,
                    isGarageLevelTitleValid: { _, title, spots in
                        isGarageLevelCreationValid(title, spots)
                    },
                    isParkingSpotCreationValid: isParkingSpotCreationValid,
                    save: { garageLevel in
                        let createGarageLevel: CreateGarageLevel = Di.shared.get()
                        let either = try await createGarageLevel.invoke(request: GarageLevelRequest(garageLevel))
                        _ = try SwiftEither(either: either).get()
                    }
                )
            }
        }
    }
}

extension AlertState where Action == SlotsListFeature.Action.Alert {
    static func deleteConfirmation(with id: SlotItemFeature.State.ID) -> AlertState {
        return AlertState {
            TextState("Are you sure you want delete this whole garage level?")
        } actions: {
            ButtonState(action: .deleteConfirmation(id)) {
                TextState("Yes")
            }
            ButtonState(role: .cancel) {
                TextState("No")
            }
        }
    }

    static let successfullDeletionConfirmation = AlertState {
        TextState("You successfully deleted garage level!")
    }

    static let failedDeletionConfirmation = AlertState {
        TextState("Garage level deletion failed!")
    }
}
