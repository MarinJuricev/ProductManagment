import Shared
import ComposableArchitecture
import Dependencies
import SwiftUI
import Utilities
import Core
import CommonUI

@Reducer
public struct ParkingReservationDashboardFeature: Sendable {
    public init() {}

    @Dependency(\.dashboardOptionsClient) var dashboardOptionsClient

    @ObservableState
    public struct State: Equatable, Sendable {
        var user: IAUser
        var dashboard: LoadableState<DashboardFeature<ParkingDashboardOption>.State>
        @Presents var destination: Destination.State?

        public init(user: IAUser,
                    dashboard: LoadableState<DashboardFeature<ParkingDashboardOption>.State> = .loading,
                    destination: Destination.State? = nil) {
            self.user = user
            self.dashboard = dashboard
            self.destination = destination
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(ViewAction)
        case dashboard(DashboardFeature<ParkingDashboardOption>.Action)
        case destination(PresentationAction<Destination.Action>)
        case dashboardItemsResponse(Result<[ParkingDashboardOption], Error>)
        case currentUserChanged(IAUser)

        @CasePathable
        public enum ViewAction: Sendable {
            case onTask
            case retryButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce<State, Action> { state, action in
            switch action {
            case .view(.onTask):
                return getDashboardItems(&state)

            case .view(.retryButtonTapped):
                return getDashboardItems(&state)

            case .currentUserChanged(let updatedUser):
                let oldRole = state.user.role
                state.user = updatedUser
                guard oldRole != updatedUser.role else {
                    return .none
                }
                state.destination = nil
                return getDashboardItems(&state)

            case .dashboardItemsResponse(.success(let items)):
                state.dashboard = .loaded(DashboardFeature.State(options: IdentifiedArray(uniqueElements: items)))
                return .none

            case .dashboardItemsResponse(.failure):
                state.dashboard = .failed
                return .none
                
            case .dashboard(.delegate(.optionTapped(let parkingDashboardOption))):
                switch onEnum(of: parkingDashboardOption) {
                case .myReservationsOption:
                    state.destination = .myReservations(MyReservationsFeature.State())
                case .newRequestOption:
                    state.destination = .newRequest(NewRequestFeature.State(user: state.user))
                case .userRequestsOption:
                    state.destination = .userRequests(UserRequestsFeature.State())
                case .slotsManagementOption:
                    state.destination = .slotsManagement(SlotsManagementFeature.State())
                case .emailTemplatesOption:
                    state.destination = .emailTemplates(EmailTemplatesFeature.State())
                }
                return .none
            case .dashboard:
                return .none
            case .destination:
                return .none
            }
        }
        .ifLet(\.dashboard.loaded, action: \.dashboard) {
            DashboardFeature()
        }
        .ifLet(\.$destination, action: \.destination)
    }

    private func getDashboardItems(_ state: inout State) -> EffectOf<Self> {
        state.dashboard = .loading
        return .run { [user = state.user] send in
            await send(.dashboardItemsResponse(Result { try await dashboardOptionsClient.get(for: user) }))
        }
    }
}

extension ParkingReservationDashboardFeature {
    @Reducer(state: .equatable, .sendable, action: .sendable)
    public enum Destination {
        case newRequest(NewRequestFeature)
        case userRequests(UserRequestsFeature)
        case myReservations(MyReservationsFeature)
        case slotsManagement(SlotsManagementFeature)
        case emailTemplates(EmailTemplatesFeature)
    }
}
