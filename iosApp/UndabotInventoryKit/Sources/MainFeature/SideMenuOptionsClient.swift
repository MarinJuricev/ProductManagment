import Dependencies
import DependenciesMacros
import Core
import Shared

@DependencyClient
public struct SideMenuOptionsClient {
    var getOptions: (IAUser) -> [SideMenuOption] = { _ in [] }
}

extension SideMenuOptionsClient: DependencyKey {
    public static let liveValue: SideMenuOptionsClient = SideMenuOptionsClient { user in
        let getOptions: GetMenuOptions = Di.shared.get()
        return getOptions.invoke(user: InventoryAppUser(user)).map { SideMenuOption($0) }
    }
}

extension DependencyValues {
    public var sideMenuOptionsClient: SideMenuOptionsClient {
        get { self[SideMenuOptionsClient.self] }
        set { self[SideMenuOptionsClient.self] = newValue }
    }
}
