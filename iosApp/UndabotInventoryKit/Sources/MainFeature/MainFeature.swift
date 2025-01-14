import Shared
import ComposableArchitecture
import Dependencies
import ParkingReservationFeature
import SeatReservationFeature
import CrewManagementFeature
import Foundation
import AuthenticationClient
import Core

@Reducer
public struct MainFeature: Sendable {

    public init() {}

    @Dependency(\.authenticationClient.logout) var logout
    @Dependency(\.currentUserClient) var currentUserClient
    @Dependency(\.sideMenuOptionsClient) var sideMenuOptionsClient

    @ObservableState
    public struct State: Equatable, Sendable {
        var user: IAUser
        var selectedMenuFeature: InventoryAppFeature.State

        var sideMenu: SideMenuFeature.State
        @Shared var selectedMenuOption: SideMenuOption
        @Shared var isSideMenuPresented: Bool
        @Shared var options: [SideMenuOption]

        @Presents var destination: Destination.State?

        public init(
            user: IAUser
        ) {
            self.user = user
            let sharedIsSideMenuPresented = Shared(false)
            self._isSideMenuPresented = sharedIsSideMenuPresented

            @Dependency(\.sideMenuOptionsClient) var sideMenuOptionsClient
            let sharedOptions = Shared(sideMenuOptionsClient.getOptions(user))
            self._options = sharedOptions

            let sharedSelectedMenuOption = Shared(SideMenuOption.parkingReservation)
            self._selectedMenuOption = sharedSelectedMenuOption
            switch sharedSelectedMenuOption.wrappedValue {
            case .testDevices:
                self.selectedMenuFeature = .testDevices
            case .parkingReservation:
                self.selectedMenuFeature = .parkingReservation(ParkingReservationDashboardFeature.State(user: user))
            case .seatReservation:
                self.selectedMenuFeature = .seatReservation(SeatReservationMainFeature.State(user: user))
            case .crewManagement:
                self.selectedMenuFeature = .crewManagement(CrewManagementFeature.State())
            }

            self.sideMenu = SideMenuFeature.State(isPresented: sharedIsSideMenuPresented, selectedFeature: sharedSelectedMenuOption, features: sharedOptions)
        }

        public init(
            user: IAUser,
            selectedFeature: SideMenuOption = .parkingReservation,
            selectedMenuFeature: InventoryAppFeature.State,
            parkingReservation: ParkingReservationDashboardFeature.State,
            seatReservation: SeatReservationMainFeature.State,
            crewManagement: CrewManagementFeature.State,
            isSideMenuPresented: Bool = false,
            options: [SideMenuOption]) {
                self.user = user
                let sharedSelectedFeature = Shared(selectedFeature)
                let sharedIsSideMenuPresented = Shared(isSideMenuPresented)
                let sharedOptions = Shared(options)
                self._isSideMenuPresented = sharedIsSideMenuPresented
                self._options = sharedOptions
                self._selectedMenuOption = sharedSelectedFeature
                self.selectedMenuFeature = selectedMenuFeature
                self.sideMenu = SideMenuFeature.State(isPresented: sharedIsSideMenuPresented, selectedFeature: sharedSelectedFeature, features: sharedOptions)

            }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case sideMenu(SideMenuFeature.Action)
        case view(ViewAction)
        case binding(BindingAction<State>)
        case delegate(Delegate)
        case destination(PresentationAction<Destination.Action>)
        case selectedMenuFeature(InventoryAppFeature.Action)
        case currentUserChanged(IAUser)
        case selectedSideMenuOptionChanged(SideMenuOption)

        @CasePathable
        public enum ViewAction: Sendable {
            case logoutTapped
            case sideMenuButtonTapped
            case onTask
        }

        @CasePathable
        public enum Delegate: Sendable {
            case didLogout
        }

        public enum Alert: Sendable {
            case logoutConfirmation
            case logoutCancellation
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Scope(state: \.selectedMenuFeature, action: \.selectedMenuFeature) {
            InventoryAppFeature.body
        }

        Scope(state: \.sideMenu, action: \.sideMenu) {
            SideMenuFeature()
        }

        Reduce { state, action in
            switch action {
            case .view(.logoutTapped):
                state.destination = .alert(.logout)
                return .none
            case .view(.sideMenuButtonTapped):
                state.isSideMenuPresented.toggle()
                return .none
            case .view(.onTask):
                return .merge(
                    .run { send in
                        for try await updatedUser in currentUserClient.observe() {
                            await send(.currentUserChanged(updatedUser))
                        }
                    },
                    .publisher {
                        state.$selectedMenuOption.publisher.map(Action.selectedSideMenuOptionChanged)
                    }
                )

            case .destination(.presented(.alert(.logoutConfirmation))):
                return .run { send in
                    try? await logout()
                    await send(.delegate(.didLogout))
                }

            case .currentUserChanged(let updatedUser):
                state.user = updatedUser
                let options = sideMenuOptionsClient.getOptions(state.user)
                state.options = options
                state.handleSideMenuOptionsChange(options)
                switch state.selectedMenuFeature {
                case .testDevices, .crewManagement:
                    return .none
                case .parkingReservation:
                    return .send(.selectedMenuFeature(.parkingReservation(.currentUserChanged(updatedUser))))
                case .seatReservation:
                    return .send(.selectedMenuFeature(.seatReservation(.currentUserChanged(updatedUser))))
                }

            case .selectedSideMenuOptionChanged:
                state.handleSideMenuOptionChange()
                return .none

            case .selectedMenuFeature:
                return .none
            case .binding:
                return .none
            case .delegate:
                return .none
            case .sideMenu:
                return .none
            case .destination:
                return .none
            }
        }
        .ifLet(\.$destination, action: \.destination)
    }
}

extension MainFeature {
    @Reducer(state: .sendable, .equatable, action: .sendable)
    public enum Destination {
        case alert(AlertState<MainFeature.Action.Alert>)
    }

    @Reducer(state: .sendable, .equatable, action: .sendable)
    public enum InventoryAppFeature {
        case testDevices
        case parkingReservation(ParkingReservationDashboardFeature)
        case seatReservation(SeatReservationMainFeature)
        case crewManagement(CrewManagementFeature)
    }
}

extension AlertState where Action == MainFeature.Action.Alert {
    public static let logout = AlertState {
        TextState("Logout?")
    } actions: {
        ButtonState(action: .logoutCancellation) {
            TextState("Cancel")
        }
        ButtonState(action: .logoutConfirmation) {
            TextState("Logout")
        }
    } message: {
        TextState("Do you really want to logout?")
    }
}
