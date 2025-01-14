import ComposableArchitecture
import SwiftUI
import Utilities
import CommonUI

@ViewAction(for: ValidatableTextFieldFeature.self)
public struct BorderedValidatableTextField: View {
    @Bindable public var store: StoreOf<ValidatableTextFieldFeature>

    public init(store: StoreOf<ValidatableTextFieldFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            VStack(alignment: .leading, spacing: 4) {
                TextField(store.placeholder, text: $store.text)
                    .multilineTextAlignment(.leading)
                    .font(Font(resource: \.montserrat_medium, size: 12))
                    .foregroundStyle(Color(resource: \.textBlack))
                    .lineLimit(1)
                    .padding(14)
                    .overlay {
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color(resource: \.textLight), lineWidth: 1)
                    }

                Text(store.errorMessage ?? " ")
                    .font(Font(resource: \.montserrat_regular, size: 8))
                    .foregroundStyle(Color(resource: \.error))
            }
            .onFirstAppear {
                send(.onFirstAppear)
            }

        }
    }
}
