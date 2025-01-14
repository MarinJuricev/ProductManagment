import ComposableArchitecture
import SwiftUI
import CommonUI
import Utilities
import Shared

@ViewAction(for: CrewManagementFeature.self)
public struct CrewManagementView: View {
    @Bindable public var store: StoreOf<CrewManagementFeature>

    public init(store: StoreOf<CrewManagementFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)
                VStack {
                    switch store.crewData {
                    case .initial:
                        EmptyView()
                    case .loading:
                        ProgressView()
                            .progressViewStyle(.circular)
                    case .loaded(let users):
                        ScrollView {
                            VStack {
                                createUserButton
                                ForEach(users) { user in
                                    Button {
                                        send(.onUserTapped(user))
                                    } label: {
                                        HStack {
                                            CircularImage(url: user.profileImageUrl)
                                            Text(user.email.username)
                                                .font(Font(resource: \.montserrat_regular, size: 12))
                                                .foregroundStyle(Color(resource: \.textBlack))
                                            Spacer()
                                        }
                                        .padding()
                                        .background(Color.white)
                                        .clipShape(.rect(cornerRadius: 23))
                                    }
                                }
                            }
                            .padding(24)
                        }
                    case .failed:
                        Button(action: {
                            send(.retryButtonTapped)
                        }, label: {
                            Text(resource: \.general_retry_button_text)
                        })
                    }
                }
            }
            .task {
                send(.onTask)
            }
            .sheet(item: $store.scope(state: \.destination?.editForm, action: \.destination.editForm)) { store in
                NavigationView {
                    CrewFormView(store: store)
                        .navigationTitle(MR.strings().crew_management_edit_user_sheet_title.desc().localized())
                        .navigationBarTitleDisplayMode(.inline)
                        .interactiveDismissDisabled()
                }
                .presentationDetents([.height(300)])
            }
            .sheet(item: $store.scope(state: \.destination?.createForm, action: \.destination.createForm)) { store in
                NavigationView {
                    CrewFormView(store: store)
                        .navigationTitle(MR.strings().crew_management_create_user_sheet_title.desc().localized())
                        .navigationBarTitleDisplayMode(.inline)
                        .interactiveDismissDisabled()
                }
                .presentationDetents([.height(300)])
            }
        }
    }

    @ViewBuilder
    var createUserButton: some View {
        Button {
            send(.onCreateUserTapped)
        } label: {
            HStack {
                Text(resource: \.crew_management_create_new_user)
                    .font(Font(resource: \.montserrat_semibold, size: 14))
                    .foregroundStyle(Color(resource: \.textBlack))
                Spacer()
            }
            .padding()
            .background(Color.white)
            .clipShape(.rect(cornerRadius: 23))
        }
    }
}
