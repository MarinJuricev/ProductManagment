import ComposableArchitecture
import CommonUI
import Utilities
import SwiftUI
import Shared

@ViewAction(for: UserRequestDetailsFeature.self)
public struct UserRequestDetailsView: View {
    @Bindable public var store: StoreOf<UserRequestDetailsFeature>
    @FocusState var focus: UserRequestDetailsFeature.State.Field?

    public init(
        store: StoreOf<UserRequestDetailsFeature>,
        focus: UserRequestDetailsFeature.State.Field? = nil
    ) {
        self.store = store
        self.focus = focus
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                VStack(spacing: 20) {
                    statusMenu()

                    VStack(alignment: .leading, spacing: 10) {
                        Text(resource: \.parking_reservation_new_request_date)
                            .font(Font(resource: \.montserrat_semibold, size: 12))
                            .foregroundStyle(Color(resource: \.secondary))

                        PreviewDatePickerView(date: store.parkingReservation.date)
                    }

                    VStack(alignment: .leading, spacing: 10) {
                        Text(resource: \.parking_reservation_new_request_additional_notes)
                            .font(Font(resource: \.montserrat_semibold, size: 12))
                            .foregroundStyle(Color(resource: \.secondary))

                        PreviewTextField(text: store.parkingReservation.note)
                    }

                    adminNoteTextField()
                    parkingCoordinates()
                    saveButton()
                }
                .padding(20)
                .task {
                    send(.onTask)
                }
                .animation(.easeInOut, value: store.shouldShowSaveButton)
                .animation(.easeInOut, value: store.status)
                .bind($store.focus, to: $focus)
                .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
                
                if store.isRequestInFlight {
                    ProgressView()
                                  .progressViewStyle(.circular)
                }

            }
        }
    }

    var adminNoteTitle: KeyPath<MR.strings, StringResource>? {
        switch store.status {
        case .approved:
            \.parking_reservation_item_approve_note
        case .declined:
            \.parking_reservation_item_reject_reason
        case .cancelled, .submitted:
            nil
        }
    }

    @ViewBuilder
    func statusMenu() -> some View {
        Menu {
            Picker("", selection: $store.userRequestStatus) {
                ForEach(IAParkingReservationStatusType.userRequestCases, id: \.self) { status in
                    Text(status.title).tag(status)
                }
            }
        } label: {
            StatusPickerLabel(title: store.status.title, backgroundColor: store.status.color)
        }
    }

    @ViewBuilder
    func adminNoteTextField() -> some View {
        if let adminNote = store.status.approved?.adminNote ?? store.status.declined,
           let adminNoteTitle = adminNoteTitle {
            VStack(alignment: .leading, spacing: 16) {
                Text(resource: adminNoteTitle)
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .foregroundStyle(Color(resource: \.secondary))

                TextField(MR.strings().parking_reservation_new_request_additional_notes_placeholder.desc().localized(), text: .init(get: {
                    adminNote
                }, set: { newAdminNote in
                    if store.status.is(\.approved) {
                        store.status.modify(\.approved) { $0.adminNote = newAdminNote }
                    } else {
                        store.status.modify(\.declined) { $0 = newAdminNote }
                    }
                }), axis: .vertical)
                .focused($focus, equals: .adminNote)
                .font(Font(resource: \.montserrat_regular, size: 12))
                .foregroundStyle(Color(resource: \.textBlack))
                .lineLimit(6, reservesSpace: true)
                .textFieldStyle(.roundedBorder)
                .tint(Color(resource: \.textLight))
            }
        }
    }

    @ViewBuilder
    func saveButton() -> some View {
        if store.shouldShowSaveButton {
            Button {
                send(.saveButtonTapped)
            } label: {
                Text(resource: \.parking_reservation_user_request_save)
                    .padding(8)
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .foregroundStyle(Color(resource: \.surface))
                    .frame(maxWidth: .infinity)
            }
            .disabled(store.isSaveButtonDisabled)
            .tint(Color(resource: \.secondary))
            .buttonStyle(.borderedProminent)
            .buttonBorderShape(.roundedRectangle(radius: 12))
        }
    }

    @ViewBuilder
    func parkingCoordinates() -> some View {
        if store.shouldShowParkingCoordinates, let reservationApprovalStore = store.scope(state: \.reservationApproval, action: \.reservationApproval) {
            ReservationApprovalView(store: reservationApprovalStore)
                .frame(maxWidth: .infinity)
        }
    }
}
