import ComposableArchitecture
import Dependencies
import Utilities
import Shared
import Foundation
import Core

@Reducer
public struct ParkingReservationsFeature: Sendable {

    @Dependency(\.parkingReservationsClient) var parkingReservationsClient

    @ObservableState
    public struct State: Equatable, Sendable {
        var startDate: Date
        var endDate: Date
        var data: LoadableState<IdentifiedArrayOf<IAParkingReservation>>
        var isFromDatePickerVisible: Bool = false
        var isToDatePickerVisible: Bool = false
        @Shared var searchText: String
        @Shared var statusFilter: IAParkingReservationStatusType?

        var startDateRange: PartialRangeThrough<Date> {
            ...endDate
        }

        var endDateRange: PartialRangeFrom<Date> {
            startDate...
        }

        var filteredData: IdentifiedArrayOf<IAParkingReservation> {
            return data.loaded?
                .filter {
                    guard !searchText.isEmpty else { return true }
                    return $0.email.username.lowercased().contains(searchText.lowercased())
                }
                .filter {
                    guard let statusFilter else { return true }
                    return statusFilter == IAParkingReservationStatusType($0.status)
                } ?? []
        }

        public init(
            data: LoadableState<IdentifiedArrayOf<IAParkingReservation>> = .initial,
            searchText: Shared<String> = Shared(""),
            statusFilter: Shared<IAParkingReservationStatusType?> = Shared(nil)
        ) {
            @Dependency(\.date) var date
            self.startDate = date.now
            self.endDate = date.now.endOfMonth()
            self.data = data
            self._searchText = searchText
            self._statusFilter = statusFilter
        }

        @CasePathable
        public enum DatePickerType: Sendable {
            case from
            case to
        }

        mutating func closeDatePickers() {
            isFromDatePickerVisible = false
            isToDatePickerVisible = false
        }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case binding(BindingAction<State>)
        case view(ViewAction)
        case delegate(Delegate)
        case parkingReservationResponse(Result<[IAParkingReservation], Error>)

        @CasePathable
        public enum ViewAction: Sendable {
            case onTask
            case retryButtonTapped
            case itemTapped(IAParkingReservation)
            case fromDatePickerTapped
            case toDatePickerTapped
        }

        @CasePathable
        public enum Delegate: Sendable {
            case itemTapped(IAParkingReservation)
        }
    }

    enum CancelID: Hashable {
        case getParkingReservations
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.fromDatePickerTapped):
                state.isFromDatePickerVisible = true
                return .none

            case .view(.toDatePickerTapped):
                state.isToDatePickerVisible = true
                return .none
                
            case .view(.onTask):
                return getParkingReservations(&state)

            case .view(.retryButtonTapped):
                return getParkingReservations(&state)

            case .view(.itemTapped(let item)):
                return .send(.delegate(.itemTapped(item)))

            case .parkingReservationResponse(.success(let parkingReservations)):
                state.data = .loaded(IdentifiedArray(uniqueElements: parkingReservations))
                return .none

            case .parkingReservationResponse(.failure):
                state.data = .failed
                return .none

            case .binding(\.startDate), .binding(\.endDate):
                state.closeDatePickers()
                return getParkingReservations(&state)
                
            case .binding:
                return .none

            case .delegate:
                return .none
            }
        }
    }

    private func getParkingReservations(_ state: inout State) -> EffectOf<Self> {
        state.data = .loading
        let startDate = Int64(state.startDate.millisecondsSince1970)
        let endDate = Int64(state.endDate.millisecondsSince1970)

        return .run { send in
            for try await reservations in parkingReservationsClient.observe(startDate, endDate) {
                await send(.parkingReservationResponse(.success(reservations)))
            }
        } catch: { error, send in
            await send(.parkingReservationResponse(.failure(error)))
        }
        .cancellable(id: CancelID.getParkingReservations, cancelInFlight: true)
    }
}
