import ComposableArchitecture
import SwiftUI
import CommonUI
import Utilities
import Core
import Shared

@ViewAction(for: CrewFormFeature.self)
public struct CrewFormView: View {
    @Bindable public var store: StoreOf<CrewFormFeature>

    public init(
        store: StoreOf<CrewFormFeature>
    ) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                VStack(spacing: 16) {
                    CircularImage(url: store.user.profileImageUrl)
                    HStack(alignment: .center) {
                        Text(resource: \.crew_management_form_email_title)
                            .font(Font(resource: \.montserrat_semibold, size: 10))
                            .foregroundStyle(Color(resource: \.secondary))
                            .backgroundStyle(Color.red)
                            .frame(alignment: .center)

                        ValidatableTextField(store: store.scope(state: \.emailTextField, action: \.emailTextField))
                            .keyboardType(.emailAddress)
                            .padding(.bottom, -18)
                            .disabled(store.isEmailDisabled)
                    }

                    HStack {
                        Text(resource: \.crew_management_form_role)
                            .font(Font(resource: \.montserrat_semibold, size: 10))
                            .foregroundStyle(Color(resource: \.secondary))
                        Spacer()

                        BorderedPickerView(values: IAUser.IARole.allCases, selectedValue: $store.role)
                            .font(Font(resource: \.montserrat_semibold, size: 10))
                            .foregroundStyle(Color(resource: \.secondary))

                    }

                    Toggle(MR.strings().crew_management_form_garage_access.desc().localized(), isOn: $store.user.hasPermanentGarageAccess)
                        .font(Font(resource: \.montserrat_semibold, size: 10))
                        .foregroundStyle(Color(resource: \.secondary))

                    Spacer()
                }
                .opacity(store.isRequestInFlight ? 0 : 1)
                .disabled(store.isRequestInFlight)
                if store.isRequestInFlight {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
            .padding()
            .background(Color.white)
            .clipShape(.rect(cornerRadius: 23))
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
                    .disabled(store.isSaveButtonDisabled)
                }

            }
            .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
        }
    }
}

extension IAUser.IARole: PickableItem {}
