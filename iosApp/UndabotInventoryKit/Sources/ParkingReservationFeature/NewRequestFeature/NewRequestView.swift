import ComposableArchitecture
import SwiftUI
import CommonUI
import Shared
import Utilities

@ViewAction(for: NewRequestFeature.self)
public struct NewRequestView: View {
    @Bindable public var store: StoreOf<NewRequestFeature>
    @FocusState var focus: NewRequestFeature.State.Field?

    public init(
        store: StoreOf<NewRequestFeature>,
        focus: NewRequestFeature.State.Field? = nil
    ) {
        self.store = store
        self.focus = focus
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea()
                    .onTapGesture {
                        send(.hideKeyboard)
                    }
                VStack {
                    VStack(alignment: .leading, spacing: 16) {
                        HStack(spacing: 8) {
                            Text(resource: \.parking_reservation_new_request_as)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.textBlack))
                                .padding(.trailing, 14)
                            HStack {
                                CircularImage(url: store.user.profileImageUrl)
                                VStack(alignment: .leading) {
                                    Text(store.user.email.username)
                                        .font(Font(resource: \.montserrat_regular, size: 12))
                                        .foregroundStyle(Color(resource: \.textBlack))
                                        .lineLimit(1)
                                    Text(store.user.role.title)
                                        .font(Font(resource: \.montserrat_semibold, size: 12))
                                        .foregroundStyle(Color(resource: \.secondary))
                                }
                                
                                if store.isReservation {
                                    Image(resource: \.ic_edit_user)
                                        .resizable()
                                        .frame(width: 12, height: 12)
                                }
                            }
                            .onTapGesture {
                                send(.onUserPickerTapped)
                            }
                        }
                        .padding(.bottom, 8)

                        if store.isReservation {
                            Text(resource: \.parking_reservation_new_request_date)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))
                            Button {
                                send(.datePickerTapped, animation: .easeInOut)
                            } label: {
                                PreviewDatePickerView(date: store.date)
                            }
                            .datePicker(isPresented: $store.isDatePickerVisible, date: $store.date)
                            .padding(.bottom, 8)
                        } else {
                            Text(resource: \.parking_reservation_dates)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))

                            datesGrid()
                        }

                        Text(noteTitle)
                            .font(Font(resource: \.montserrat_semibold, size: 12))
                            .foregroundStyle(Color(resource: \.secondary))

                        TextField(MR.strings().parking_reservation_new_request_additional_notes_placeholder.desc().localized(), text: $store.note, axis: .vertical)
                            .focused($focus, equals: .adminNote)
                            .font(Font(resource: \.montserrat_regular, size: 12))
                            .foregroundStyle(Color(resource: \.textBlack))
                            .lineLimit(6, reservesSpace: true)
                            .textFieldStyle(.roundedBorder)
                            .tint(Color(resource: \.textLight))

                        VStack(alignment: .center) {
                            parkingCoordinates()
                        }

                        Button {
                            send(.submitTapped)
                        } label: {
                            Text(resource: \.parking_reservation_new_request_submit)
                                .padding(8)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.surface))
                                .frame(maxWidth: .infinity)
                        }
                        .disabled(store.isSubmitButtonDisabled)
                        .tint(Color(resource: \.secondary))
                        .buttonStyle(.borderedProminent)
                        .buttonBorderShape(.roundedRectangle(radius: 12))
                        .padding(.top, 20)

                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 28)
                }
                .buttonStyle(PlainButtonStyle())
                .background(Color.white)
                .clipShape(.rect(cornerRadius: 34))
                .padding(.horizontal, 16)
                if store.isRequestInFlight {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
            .task {
                send(.onTask)
            }
            .bind($store.focus, to: $focus)
            .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
            .sheet(item: $store.scope(state: \.destination?.usersPicker, action: \.destination.usersPicker)) { store in
                NavigationView {
                    UsersPickerView(store: store)
                        .presentationDetents([.large])
                }

            }
            .sheet(item: $store.scope(state: \.destination?.multiRequestResponse, action: \.destination.multiRequestResponse)) { store in
                MultiRequestResponseView(store: store)
                    .presentationDetents([.medium])
                    .interactiveDismissDisabled()
            }
        }
    }

    var noteTitle: String {
        store.isReservation ? MR.strings().parking_reservation_item_approve_note.desc().localized() : MR.strings().parking_reservation_new_request_additional_notes.desc().localized()
    }

    @ViewBuilder
    func parkingCoordinates() -> some View {
        if store.isReservation, let reservationApprovalStore = store.scope(state: \.reservationApproval, action: \.reservationApproval) {
            ReservationApprovalView(store: reservationApprovalStore)
                .frame(maxWidth: .infinity)
        }
    }

    @ViewBuilder
    func datesGrid() -> some View {
        if let store = store.scope(state: \.multiDatePicker, action: \.multiDatePicker) {
            MultiDatePickerView(store: store)
        }
    }
}
