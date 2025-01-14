import ComposableArchitecture
import Shared
import Utilities
import Foundation
import Core

@Reducer
public struct SlotsManagementFeature: Sendable {

    @Dependency(\.garageLevelsClient) var garageLevelsClient

    @ObservableState
    public struct State: Equatable, Sendable {
        var garageLevelData: LoadableState<SlotsListFeature.State>

        public init(garageLevelData: LoadableState<SlotsListFeature.State> = .initial) {
            self.garageLevelData = garageLevelData        }
    }

    public enum Action: ViewAction, Sendable {
        case view(View)
        case garageLevelsResponse(Result<[IAGarageLevelData], Error>)
        case garageLevelData(SlotsListFeature.Action)

        public enum View: Sendable {
            case onTask
            case retryButtonTapped
        }
    }

    enum CancelID: Hashable {
        case getSlots
    }

    public var body: some ReducerOf<Self> {
        Reduce<State, Action> { state, action in
            switch action {
            case .view(.onTask):
                return fetchGarageLevels(state: &state)

            case .view(.retryButtonTapped):
                return fetchGarageLevels(state: &state)

            case .garageLevelsResponse(.success(let garageLevels)):
                let slotItemFeatures = garageLevels.map { garageLevel in
                    let isExpanded = state.garageLevelData.loaded?.slots[id: garageLevel.id]?.isExpanded ?? false
                    return SlotItemFeature.State(garageLevelData: garageLevel, isExpanded: isExpanded) }

                if state.garageLevelData.is(\.loaded) {
                    state.garageLevelData.modify(\.loaded) { $0.slots = IdentifiedArray(uniqueElements: slotItemFeatures) }
                } else {
                    state.garageLevelData = .loaded(SlotsListFeature.State(slots: IdentifiedArray(uniqueElements: slotItemFeatures)))
                }
                return .none

            case .garageLevelsResponse(.failure):
                state.garageLevelData = .failed
                return .none

            case .garageLevelData:
                return .none
            }
        }
        .ifLet(\.garageLevelData.loaded, action: \.garageLevelData) {
            SlotsListFeature()
        }
    }

    private func fetchGarageLevels(state: inout State) -> EffectOf<Self> {
        state.garageLevelData = .loading
        return .run { send in
            for try await garageLevels in garageLevelsClient.observeLevels() {
                await send(.garageLevelsResponse(.success(garageLevels)))
            }
        } catch: { error, send in
            await send(.garageLevelsResponse(.failure(error)))
        }.cancellable(id: CancelID.getSlots, cancelInFlight: true)
    }
}
