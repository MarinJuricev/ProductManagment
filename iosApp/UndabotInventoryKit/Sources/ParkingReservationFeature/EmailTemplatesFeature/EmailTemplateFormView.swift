import ComposableArchitecture
import SwiftUI
import WebKit
import Shared

@ViewAction(for: EmailTemplateFormFeature.self)
public struct EmailTemplateFormView: View {
    @Bindable public var store: StoreOf<EmailTemplateFormFeature>

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)

                QuillEditorWebView(text: $store.template.text)

                if store.isRequestInFlight {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
            .navigationTitle(store.template.title)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(MR.strings().general_save.desc().localized()) {
                        send(.onSaveButtonTapped)
                    }
                    .disabled(store.isSaveButtonDisabled)
                }
            }
            .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
        }
    }
}
