import ComposableArchitecture
import Foundation
import Core
import Utilities

@Reducer
public struct MultiDatePickerFeature {

    @Dependency(\.parkingReservationsClient.observe) var observeMyReservations

    @ObservableState
    public struct State: Equatable, Sendable {
        var startDate: Date
        var endDate: Date
        var dates: [DateComponents] = []
        var alreadyReservedDates: LoadableState<[Date]> = .loading
        var isCalendarPresented: Bool = false

        var excludedDates: [Date] {
            let alreadyReservedDates = alreadyReservedDates.loaded ?? []
            return dates.compactMap { $0.date } + alreadyReservedDates
        }

        var shouldShowAddButton: Bool {
            dates.count < 5
        }

        var isValid: Bool {
            dates.count > 0 && dates.count <= 5
        }

        init(
            startDate: Date
        ) {
            self.startDate = startDate
            self.endDate = startDate.add(days: 14)
        }
    }

    public enum Action: ViewAction, BindableAction, Sendable {
        case binding(BindingAction<State>)
        case view(View)
        case alreadyReservedDatesRespose(Result<[Date], Error>)

        public enum View: Sendable {
            case onTask
            case onRetryButtonTapped
            case onAddButtonTapped
            case onRemoveButtonTapped(DateComponents)
        }
    }

    public var body: some ReducerOf<Self> {
        BindingReducer()

        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return observeAlreadyReservedDates(state: &state)

            case .view(.onRetryButtonTapped):
                return observeAlreadyReservedDates(state: &state)

            case .alreadyReservedDatesRespose(.success(let alreadyReservedDates)):
                state.alreadyReservedDates = .loaded(alreadyReservedDates)
                return .none

            case .alreadyReservedDatesRespose(.failure):
                state.alreadyReservedDates = .failed
                return .none

            case .view(.onAddButtonTapped):
                state.isCalendarPresented = true
                return .none
            case .view(.onRemoveButtonTapped(let date)):
                guard let index = state.dates.firstIndex(of: date) else { return .none }
                state.dates.remove(at: index)
                return .none
            case .binding(\.dates):
                state.isCalendarPresented = false
                return .none
            case .binding:
                return .none
            }
        }
    }

    func observeAlreadyReservedDates(state: inout State) -> EffectOf<Self> {
        state.alreadyReservedDates = .loading
        let start = state.startDate.millisecondsSince1970
        let end = state.endDate.millisecondsSince1970
        return .run { send in
            for try await reservations in observeMyReservations(start, end) {
                let dates = reservations.map { $0.date }
                await send(.alreadyReservedDatesRespose(.success(dates)))
            }
        } catch: { error, send in
            await send(.alreadyReservedDatesRespose(.failure(error)))
        }
    }
}
