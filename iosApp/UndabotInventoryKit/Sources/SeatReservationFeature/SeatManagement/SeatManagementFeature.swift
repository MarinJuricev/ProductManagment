import ComposableArchitecture
import Foundation
import Utilities
import CommonUI
import Shared

@Reducer
public struct SeatManagementFeature {

    @Dependency(\.officesClient)  var officesClient
    @Dependency(\.officeValidator) var officeValidator

    @ObservableState
    public struct State: Equatable, Sendable {
        var officeData: LoadableState<IdentifiedArrayOf<IAOffice>>
        var officeTitleTextField: ValidatableTextFieldFeature.State
        @Shared var newOfficeTitle: String
        var newOfficeSeatsNumber: String
        var isRequestInFlight: Bool
        var isAddButtonDisabled: Bool
        var focus: Focus?
        @Presents var destination: Destination.State?

        public init(
            officeData: LoadableState<IdentifiedArrayOf<IAOffice>> = .loading,
            newOfficeTitle: Shared<String> = Shared("")
        ) {
            self.officeData = officeData
            self._newOfficeTitle = newOfficeTitle
            self.officeTitleTextField = ValidatableTextFieldFeature.State(text: newOfficeTitle, placeholder: MR.strings().seat_reservation_office_name_placeholder.desc().localized())

            self.newOfficeSeatsNumber = ""
            self.isRequestInFlight = false
            self.isAddButtonDisabled = true
        }

        public enum Focus: Equatable, Sendable {
            case officeTitle
            case officeCapacity
        }
    }

    public enum Action: Sendable, ViewAction, BindableAction {
        case binding(BindingAction<State>)
        case view(View)
        case officesResponse(Result<[IAOffice], Error>)
        case deleteOfficeResponse(Result<Void, Error>)
        case addOfficeResponse(Result<Void, Error>)
        case destination(PresentationAction<Destination.Action>)
        case officeTitleTextField(ValidatableTextFieldFeature.Action)
        case officeTitleChanged(String)

        public enum View: Sendable {
            case onTask
            case onRetryButtonTapped
            case onDeleteButtonTapped(IAOffice)
            case onEditButtonTapped(IAOffice)
            case onAddButtonTapped
        }

        public enum Alert: Sendable, Equatable {
            case deleteConfirmation(IAOffice)
            case successfullDeletionConfirmation
        }
    }

    enum CancelID: Hashable {
        case getOffices
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        ReducerReader { state, _ in

            Scope(state: \.officeTitleTextField, action: \.officeTitleTextField) {
                ValidatableTextFieldFeature { _ in
                    let unavailableOffices = state.officeData.loaded?.elements.map { $0.title } ?? []
                    return officeValidator.validateAddTitle(unavailableTitles: unavailableOffices, title: state.newOfficeTitle)
                }
            }

            Reduce<State, Action> { state, action in
                switch action {
                case .view(.onTask):
                    return .merge(
                        fetchOffices(state: &state),
                        .publisher { state.$newOfficeTitle.publisher.map(Action.officeTitleChanged) }
                    )

                case .view(.onRetryButtonTapped):
                    return fetchOffices(state: &state)

                case .view(.onDeleteButtonTapped(let office)):
                    state.destination = .alert(.deleteConfirmation(with: office))
                    return .none

                case .view(.onEditButtonTapped(let office)):
                    state.destination = .editForm(OfficeFormFeature.State(office: Shared(office)))
                    return .none

                case .view(.onAddButtonTapped):
                    state.focus = nil
                    state.isRequestInFlight = true
                    return .run { [title = state.newOfficeTitle, seats = state.newOfficeSeatsNumber] send in
                        await send(.addOfficeResponse(Result { try await officesClient.add(title, seats) }))
                    }

                case .officesResponse(.success(let offices)):
                    state.officeData = .loaded(IdentifiedArray(uniqueElements: offices))
                    return .none

                case .officesResponse(.failure):
                    state.officeData = .failed
                    return .none

                case .destination(.presented(.alert(.deleteConfirmation(let office)))):
                    state.isRequestInFlight = true
                    return .run { send in
                        await send(.deleteOfficeResponse(Result { try await officesClient.delete(office) }))
                    }

                case .deleteOfficeResponse(.success):
                    state.isRequestInFlight = false
                    state.destination = .alert(.successfullDeletionConfirmation)
                    return .none

                case .deleteOfficeResponse(.failure):
                    state.isRequestInFlight = false
                    state.destination = .alert(.failedDeletionConfirmation)
                    return .none

                case .addOfficeResponse(.success):
                    state.isRequestInFlight = false
                    state.newOfficeTitle = ""
                    state.newOfficeSeatsNumber = ""
                    state.destination = .alert(.successfullAddingConfirmation)
                    return .none

                case .addOfficeResponse(.failure):
                    state.isRequestInFlight = false
                    state.destination = .alert(.failedAddingConfirmation)
                    return .none

                case .destination:
                    return.none

                case .officeTitleTextField:
                    return .none

                case .officeTitleChanged, .binding(\.newOfficeSeatsNumber):
                    return validateFields(state: &state)

                case .binding:
                    return .none
                }
            }
            .ifLet(\.$destination, action: \.destination) {
                Destination { initialOffice, title, seats in
                    let unavailableOffices = state.officeData.loaded?.elements ?? []
                    return officeValidator.isEditingValid(unavailableOffices: unavailableOffices, initialOffice: initialOffice, title: title, seats: seats)
                } validateOfficeTitle: { initialOffice, title in
                    let unavailableOffices = state.officeData.loaded?.elements ?? []
                    return officeValidator.validateEditTitle(unavailableOffices: unavailableOffices, initialOffice: initialOffice, title: title)
                }
            }
        }
    }

    private func fetchOffices(state: inout State) -> EffectOf<Self> {
        state.officeData = .loading
        return .run { send in
            for try await offices in officesClient.observe() {
                await send(.officesResponse(.success(offices)))
            }
        } catch: { error, send in
            await send(.officesResponse(.failure(error)))
        }.cancellable(id: CancelID.getOffices, cancelInFlight: true)
    }

    private func validateFields(state: inout State) -> EffectOf<Self> {
        let unavailableTitles = state.officeData.loaded?.elements.map { $0.title } ?? []
        state.isAddButtonDisabled = !officeValidator.isAddingValid(unavailableTitles, state.newOfficeTitle, state.newOfficeSeatsNumber)
        return .none
    }
}

extension SeatManagementFeature {
    @Reducer
    public struct Destination {

        private let validateEditing: @Sendable (IAOffice, String, String) -> Bool
        private let validateOfficeTitle: @Sendable (IAOffice, String) -> String?

        public init(validateEditing: @Sendable @escaping (IAOffice, String, String) -> Bool,
                    validateOfficeTitle: @Sendable @escaping (IAOffice, String) -> String?
        ) {
            self.validateEditing = validateEditing
            self.validateOfficeTitle = validateOfficeTitle
        }

        public enum State: Equatable, Sendable {
            case alert(AlertState<SeatManagementFeature.Action.Alert>)
            case editForm(OfficeFormFeature.State)
        }

        public enum Action: Sendable {
            case alert(SeatManagementFeature.Action.Alert)
            case editForm(OfficeFormFeature.Action)
        }

        public var body: some ReducerOf<Self> {
            Scope(state: \.editForm, action: \.editForm) {
                OfficeFormFeature(validateEditing: validateEditing, validateTitle: validateOfficeTitle)
            }
        }

    }
}

extension AlertState where Action == SeatManagementFeature.Action.Alert {
    static func deleteConfirmation(with office: IAOffice) -> AlertState {
        return AlertState {
            TextState(deleteOffice(name: office.title).localized())
        } actions: {
            ButtonState(action: .deleteConfirmation(office)) {
                TextState(resource: \.general_yes)
            }
            ButtonState(role: .cancel) {
                TextState(resource: \.general_no_go_back)
            }
        }
    }

    static let successfullDeletionConfirmation = AlertState {
        TextState(resource: \.seat_reservation_success_delete_office_message)
    }

    static let successfullAddingConfirmation = AlertState {
        TextState(resource: \.seat_reservation_success_add_office_message)
    }

    static let failedDeletionConfirmation = AlertState {
        TextState(resource: \.seat_reservation_failure_delete_office_message)
    }

    static let failedAddingConfirmation = AlertState {
        TextState(resource: \.seat_reservation_failure_add_office_message)
    }
}
