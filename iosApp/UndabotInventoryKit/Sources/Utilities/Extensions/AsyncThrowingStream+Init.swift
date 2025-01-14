import Shared
import Foundation

public extension AsyncThrowingStream where Failure == Error {
    init(_ flow: SkieSwiftFlow<Element>) {
        self.init { continuation in
            Task {
                for await element in flow {
                    continuation.yield(element)
                }
            }
        }
    }

    init<T>(_ flow: SkieSwiftFlow<T>, mapper: @escaping (T) -> Element) {
        self.init { continuation in
            Task {
                for await element in flow {
                    continuation.yield(mapper(element))
                }
            }
        }
    }

    init<T>(_ flow: SkieSwiftFlow<T>, mapper: @escaping (T.Element) -> Element.Element) where T: Collection, Element: Collection {
        self.init { continuation in
            Task {
                for await collection in flow {
                    // swiftlint:disable:next force_cast
                    continuation.yield(collection.map { mapper($0) } as! Element)
                }
            }
        }
    }
}
