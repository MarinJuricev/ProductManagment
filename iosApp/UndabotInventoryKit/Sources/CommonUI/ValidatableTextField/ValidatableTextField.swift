import ComposableArchitecture
import SwiftUI
import Utilities

@ViewAction(for: ValidatableTextFieldFeature.self)
public struct ValidatableTextField: View {
    @Bindable public var store: StoreOf<ValidatableTextFieldFeature>

    public init(store: StoreOf<ValidatableTextFieldFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            VStack(alignment: .leading, spacing: 4) {
                TextField(store.placeholder, text: $store.text)
                    .font(Font(resource: \.montserrat_regular, size: 12))
                    .foregroundStyle(Color(resource: \.textBlack))
                    .lineLimit(1, reservesSpace: true)
                    .textFieldStyle(.roundedBorder)
                    .tint(Color(resource: \.textLight))
                
                Text(store.errorMessage ?? " ")
                    .font(Font(resource: \.montserrat_regular, size: 12))
                    .foregroundStyle(Color(resource: \.error))
            }
            .onFirstAppear {
                send(.onFirstAppear)
            }
        }
    }
}
