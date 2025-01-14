import ComposableArchitecture
import SwiftUI
import LoginFeature
import MainFeature
import Shared
import Utilities

@ViewAction(for: AppFeature.self)
public struct AppView: View {
    public let store: StoreOf<AppFeature>

    public init(environment: String?) {
        self.store = Store(initialState: AppFeature.State()) {
            AppFeature(environment: environment)
        }
    }

    public var body: some View {
        WithPerceptionTracking {
            switch store.scope(state: \.appMode, action: \.appMode).case {
            case .splash:
                SplashView()
                    .task { send(.onTask) }
                
            case .login(let loginStore):
                LoginView(store: loginStore)
            case .main(let mainStore):
                MainView(store: mainStore)
            }
        }

    }
}
