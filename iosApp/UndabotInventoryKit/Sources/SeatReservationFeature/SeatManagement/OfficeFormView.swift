import ComposableArchitecture
import SwiftUI
import Shared
import Utilities

@ViewAction(for: OfficeFormFeature.self)
public struct OfficeFormView: View {
    @Bindable public var store: StoreOf<OfficeFormFeature>
    @FocusState var focus: OfficeFormFeature.State.Focus?

    public init(store: StoreOf<OfficeFormFeature>, focus: OfficeFormFeature.State.Focus? = nil) {
        self.store = store
        self.focus = focus
    }

    public var body: some View {
        WithPerceptionTracking {
                ZStack {
                    VStack(alignment: .leading, spacing: 20) {
                        HStack(spacing: 36) {
                            Text(resource: \.seat_reservation_office_title)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))
                                .frame(maxWidth: .infinity, alignment: .leading)
                            Text(resource: \.seat_reservation_seats_title)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))
                                .frame(width: 50)
                        }
                        HStack(alignment: .top, spacing: 36) {
                            BorderedValidatableTextField(store: store.scope(state: \.officeTitleTextField, action: \.officeTitleTextField))
                                .focused($focus, equals: .officeTitle)
                            borderedTextField($store.numberOfSeats, placeholder: "", width: 50)
                                .focused($focus, equals: .officeCapacity)
                                .keyboardType(.numberPad)
                        }
                    }
                    .padding(.horizontal, 15)
                    if store.isRequestInFlight {
                        ProgressView()
                            .progressViewStyle(.circular)
                    }
                }
                .task {
                    await send(.onTask).finish()
                }
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button(MR.strings().general_cancel.desc().localized()) {
                            send(.onCancelButtonTapped)
                        }
                        .disabled(store.isRequestInFlight)
                    }
                    ToolbarItem(placement: .topBarTrailing) {
                        Button(MR.strings().general_save.desc().localized()) {
                            send(.onSaveButtonTapped)
                        }
                        .disabled(store.isSaveButtonDisabled)
                    }
                }
                .bind($store.focus, to: $focus)
                .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
        }
    }

    @ViewBuilder
    func borderedTextField(_ text: Binding<String>, placeholder: String, maxWidth: CGFloat, alignment: Alignment = .center) -> some View {
        styledTextField(text, placeholder: placeholder, textAlignment: .leading)
            .frame(maxWidth: maxWidth, alignment: alignment)
            .overlay {
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(resource: \.textLight), lineWidth: 1)
            }
    }

    @ViewBuilder
    func borderedTextField(_ text: Binding<String>, placeholder: String, width: CGFloat) -> some View {
        styledTextField(text, placeholder: placeholder, textAlignment: .center)
            .frame(width: width)
            .overlay {
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(resource: \.textLight), lineWidth: 1)
            }
    }

    @ViewBuilder
    func styledTextField(_ text: Binding<String>, placeholder: String, textAlignment: TextAlignment) -> some View {
        TextField(placeholder, text: text)
            .font(Font(resource: \.montserrat_medium, size: 12))
            .foregroundStyle(Color(resource: \.textBlack))
            .multilineTextAlignment(textAlignment)
            .lineLimit(1)
            .padding(14)
    }
}
