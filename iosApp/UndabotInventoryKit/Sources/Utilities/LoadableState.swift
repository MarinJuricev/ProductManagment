import CasePaths

@CasePathable
@dynamicMemberLookup
public enum LoadableState<T: Equatable & Sendable>: Equatable, Sendable {
    case initial
    case loading
    case loaded(T)
    case failed
}
