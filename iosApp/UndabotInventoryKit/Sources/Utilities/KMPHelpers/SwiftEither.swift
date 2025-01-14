import Shared

public class SwiftEither<Success, Failure> where Success: AnyObject, Failure: AnyObject {
    let either: Arrow_coreEither<Failure, Success>

    public init(either: Arrow_coreEither<Failure, Success>) {
        self.either = either
    }
}

extension SwiftEither where Failure: Error {
    struct ParsingError: Error {}

    public func get() throws -> Success {
        if let value = either.getOrNull() {
            return value
        } else if let error = either.leftOrNull() {
            throw error
        } else {
            throw ParsingError()
        }
    }
}
