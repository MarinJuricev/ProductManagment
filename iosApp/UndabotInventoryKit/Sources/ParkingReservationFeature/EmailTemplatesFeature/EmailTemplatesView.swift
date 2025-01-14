import ComposableArchitecture
import SwiftUI
import Utilities
import Shared

@ViewAction(for: EmailTemplatesFeature.self)
public struct EmailTemplatesView: View {
    @Bindable public var store: StoreOf<EmailTemplatesFeature>

    public init(store: StoreOf<EmailTemplatesFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)
                VStack {
                    switch store.data {
                    case .initial:
                        EmptyView()
                    case .loading:
                        ProgressView()
                            .progressViewStyle(.circular)
                    case .loaded(let templates):
                        ScrollView {
                            VStack(spacing: 24) {
                                ForEach(templates) { template in
                                    HStack {
                                        ZStack {
                                            Color(resource: \.secondary)
                                                .clipShape(.rect(topLeadingRadius: 23, bottomLeadingRadius: 23))
                                            Image(resource: \.email_templates_icon)
                                                .resizable()
                                                .aspectRatio(contentMode: .fit)
                                                .frame(width: 32, height: 32)
                                        }
                                        .frame(width: 70)

                                        Text(template.title)
                                        .font(Font(resource: \.montserrat_semibold, size: 12))
                                        .foregroundStyle(Color(resource: \.textBlack))
                                        .frame(maxWidth: .infinity, alignment: .center)
                                    }
                                    .frame(height: 70)
                                    .background(Color(resource: \.surface))
                                    .clipShape(.rect(cornerRadius: 23))
                                    .onTapGesture {
                                        send(.onTemplateTapped(template))
                                    }
//                                    .overlay(RoundedRectangle(cornerRadius: 23))
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
                .navigationDestination(item: $store.scope(state: \.destination?.form, action: \.destination.form), destination: { store in
                    EmailTemplateFormView(store: store)
                })
                .task {
                    await send(.onTask).finish()
                }
            }

        }
    }
}
