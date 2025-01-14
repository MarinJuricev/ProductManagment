import ComposableArchitecture
@preconcurrency import Shared
import Foundation

@Reducer
public struct MultiRequestResponseFeature {

    @Dependency(\.parkingReservationsClient.createRequests) var createRequests

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var dateResponses: [DateResponse]
        var parkingRequests: [ParkingRequest]

        var isDoneButtonDisabled: Bool {
            dateResponses.contains { $0.state == .loading }
        }

        public init(parkingRequests: [ParkingRequest]) {
            self.dateResponses = parkingRequests.map { DateResponse(date: Date(milliseconds: $0.date), state: .loading)}
            self.parkingRequests = parkingRequests
        }
    }

    public enum Action: ViewAction, Sendable {
        case view(View)
        case parkingRequestsResponse(Result<AsyncStream<[DateResponse]>, Error>)
        case multiRequestResponse([DateResponse])
        case delegate(Delegate)

        public enum Delegate: Sendable {
            case onDoneButtonTapped
        }

        public enum View: Sendable {
            case onTask
            case onDoneButtonTapped
        }
    }

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .view(.onTask):
                return .run { [parkingRequests = state.parkingRequests] send in
                    await send(.parkingRequestsResponse(Result { try createRequests(parkingRequests) }))
                }

            case .view(.onDoneButtonTapped):
                return .send(.delegate(.onDoneButtonTapped))

            case .parkingRequestsResponse(.success(let dateResponsesStream)):
                return .run { send in
                    for await responses in dateResponsesStream {
                        let orderedResponses = responses.sorted(by: { $0.date.compare($1.date) == .orderedAscending })
                        await send(.multiRequestResponse(orderedResponses), animation: .easeInOut)
                    }
                }

            case .parkingRequestsResponse(.failure):
                return .none

            case .multiRequestResponse(let dateResponses):
                state.dateResponses = dateResponses
                return .none

            case .delegate:
                return .none
            }
        }
    }
}
