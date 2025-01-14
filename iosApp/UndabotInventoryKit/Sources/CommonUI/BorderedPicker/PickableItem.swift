public protocol PickableItem: Identifiable, Equatable {
    var id: String { get }
    var title: String { get }
}
