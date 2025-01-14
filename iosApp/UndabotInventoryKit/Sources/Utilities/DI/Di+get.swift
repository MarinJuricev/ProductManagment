import Shared

extension Di {
  public func get<T>(
    for type: T.Type = T.self,
    parameters: [Any] = []
  ) -> T {
    self.get(
      type: type,
      parameters: parameters
    ) as! T // swiftlint:disable:this force_cast
  }
}
