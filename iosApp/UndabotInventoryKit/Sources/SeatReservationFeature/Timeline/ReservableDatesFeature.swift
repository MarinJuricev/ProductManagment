import ComposableArchitecture

@Reducer
public struct ReservableDatesFeature {

    public init() {}

    @ObservableState
    public struct State: Equatable, Sendable {
        var reservableDates: IdentifiedArrayOf<ReservableDateFeature.State>

        public init(reservableDates: IdentifiedArrayOf<ReservableDateFeature.State>) {
            self.reservableDates = reservableDates
        }
    }

    public enum Action: Sendable {
        case reservableDates(IdentifiedActionOf<ReservableDateFeature>)
    }

    public var body: some ReducerOf<Self> {
        EmptyReducer()
            .forEach(\.reservableDates, action: \.reservableDates) {
                ReservableDateFeature()
            }
    }
}
