import ComposableArchitecture
import SwiftUI
import GoogleSignIn
import Shared
import Utilities

@ViewAction(for: LoginFeature.self)
public struct LoginView: View {
    @Bindable public var store: StoreOf<LoginFeature>

    public init(store: StoreOf<LoginFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea()
                VStack {
                    VStack(spacing: 0) {
                        Text(resource: \.auth_text_welcome)
                            .font(Font(resource: \.montserrat_medium, size: 12))
                            .foregroundStyle(Color(resource: \.login_button_text))
                            .padding(.bottom, 16)
                        Image(resource: \.MarinJuricev_logo)
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .padding(.horizontal, 42)
                        HStack {
                            Color(resource: \.login_button_text)
                                .frame(height: 2)
                            Text(resource: \.auth_text_continue)
                                .layoutPriority(1)
                                .font(Font(resource: \.montserrat_medium, size: 12))
                                .foregroundStyle(Color(resource: \.login_button_text))
                            Color(resource: \.login_button_text)
                                .frame(height: 2)
                        }
                        .padding(.vertical, 54)
                        Button(action: {
                            send(.loginButtonTapped(UIApplication.shared.firstKeyWindow?.rootViewController))
                        }, label: {
                            HStack(spacing: 0) {
                                Image(resource: \.google_logo)
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(width: 36, height: 36)
                                    .padding(.horizontal, 16)
                                Text(resource: \.auth_text_login1)
                                    .font(Font(resource: \.montserrat_regular, size: 12))
                                    .foregroundStyle(Color(resource: \.login_google_button_text))
                                Text(resource: \.auth_text_login2)
                                    .font(Font(resource: \.montserrat_bold, size: 12))
                                    .foregroundStyle(Color(resource: \.login_google_button_text))
                            }
                            .frame(maxWidth: .infinity)
                            .padding(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 13)
                                    .stroke(Color(resource: \.login_button_border), lineWidth: 2)
                            )
                        })

                    }
                    .padding(.horizontal, 22)
                    .padding(.vertical, 36)
                }
                .background(Color.white)
                .clipShape(.rect(cornerRadius: 34))
                .padding(.horizontal, 16)
                .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
            }
            .onOpenURL { url in
               GIDSignIn.sharedInstance.handle(url)
             }
        }
    }
}
