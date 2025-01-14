import ComposableArchitecture
import SwiftUI
import CommonUI
import Shared
import Utilities

@ViewAction(for: UsersPickerFeature.self)
public struct UsersPickerView: View {
    @Bindable public var store: StoreOf<UsersPickerFeature>

    public init(store: StoreOf<UsersPickerFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)
                VStack {
                    switch store.usersData {
                    case .initial:
                        EmptyView()
                    case .loading:
                        ProgressView()
                            .progressViewStyle(.circular)
                    case .loaded:
                        ScrollView {
                            ForEach(store.filteredUsers) { user in
                                Button {
                                    send(.onUserTapped(user), animation: .easeInOut)
                                } label: {
                                    WithPerceptionTracking {
                                        HStack {
                                            if store.selectedUser == user {
                                                Image(systemName: "checkmark")
                                            }
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
                            .padding(.horizontal, 24)
                        }
                        .searchable(text: $store.searchText)
                        .toolbar {
                            ToolbarItem(placement: .topBarLeading) {
                                Button(MR.strings().general_cancel.desc().localized()) {
                                    send(.onCancelButtonTapped)
                                }
                            }

                            ToolbarItem(placement: .topBarTrailing) {
                                Button(MR.strings().general_save.desc().localized()) {
                                    send(.onSaveButtonTapped)
                                }
                            }

                        }
                    case .failed:
                        Button(action: {
                            send(.onRetryButtonTapped)
                        }, label: {
                            Text(resource: \.general_retry_button_text)
                        })
                    }
                }
            }
            .task {
                send(.onTask)
            }
        }
    }
}
