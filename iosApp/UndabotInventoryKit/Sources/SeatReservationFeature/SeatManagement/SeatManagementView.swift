import ComposableArchitecture
import SwiftUI
import Utilities
import Shared
import CommonUI

@ViewAction(for: SeatManagementFeature.self)
public struct SeatManagementView: View {
    @Bindable public var store: Store<SeatManagementFeature.State, SeatManagementFeature.Action>
    @FocusState var focus: SeatManagementFeature.State.Focus?
    @State var scrollViewHeight: CGFloat = .zero

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                Color(resource: \.background)
                    .ignoresSafeArea(edges: .all)
                
                switch store.officeData {
                case .initial:
                    EmptyView()
                case .loading:
                    ProgressView()
                        .progressViewStyle(.circular)
                case .loaded(let offices):
                    VStack(alignment: .leading, spacing: 0) {
                        HStack(spacing: 36) {
                            Text(resource: \.seat_reservation_office_title)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))
                                .frame(maxWidth: .infinity, alignment: .leading)
                            Text(resource: \.seat_reservation_seats_title)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))
                                .frame(width: 50)
                            Color.clear
                                .frame(width: 20)
                        }
                        .padding(.horizontal, 15)
                        .frame(height: 50)

                        ScrollView {
                            VStack(spacing: 36) {
                                addNewOfficeRow
                                ForEach(offices) { office in
                                    HStack(spacing: 36) {
                                        borderedText(office.title, maxWidth: .infinity, alignment: .leading)
                                        borderedText("\(office.numberOfSeats)", width: 50)
                                        Menu {
                                            Button(MR.strings().slots_management_level_option_edit.desc().localized()) {
                                                send(.onEditButtonTapped(office))
                                            }
                                            Button(MR.strings().slots_management_level_option_delete.desc().localized()) {
                                                send(.onDeleteButtonTapped(office))
                                            }
                                        } label: {
                                            Image(resource: \.dots)
                                                .resizable()
                                                .aspectRatio(contentMode: .fit)
                                                .frame(width: 20, height: 20)
                                                .tint(Color(resource: \.textBlack))
                                        }
                                    }
                                    .frame(height: 50)
                                }
                            }
                            .padding(15)
                            .getHeight($scrollViewHeight)
                        }
                        .animation(.easeInOut, value: scrollViewHeight)
                        .frame(maxHeight: scrollViewHeight)
                    }
                    .frame(maxWidth: .infinity, minHeight: 150)
                    .background(Color.white)
                    .clipShape(.rect(cornerRadius: 23))
                    .padding(16)
                    .sheet(item: $store.scope(state: \.destination?.editForm, action: \.destination.editForm)) { store in
                        NavigationView {
                            OfficeFormView(store: store)
                                .navigationTitle("Edit")
                                .navigationBarTitleDisplayMode(.inline)
                                .interactiveDismissDisabled()
                        }
                            .presentationDetents([.height(180)])

                    }
                    if store.isRequestInFlight {
                        Color.black.opacity(0.2)
                            .ignoresSafeArea()
                        ProgressView()
                            .progressViewStyle(.circular)
                    }

                case .failed:
                    Button(action: {
                        send(.onRetryButtonTapped)
                    }, label: {
                        Text(resource: \.general_retry_button_text)
                    })
                }
            }
            .task {
                await send(.onTask).finish()
            }
            .bind($store.focus, to: $focus)
            .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
        }
    }

    @ViewBuilder
    var addNewOfficeRow: some View {
        HStack(alignment: .top, spacing: 36) {
            BorderedValidatableTextField(store: store.scope(state: \.officeTitleTextField, action: \.officeTitleTextField))
                .focused($focus, equals: .officeTitle)
            borderedTextField($store.newOfficeSeatsNumber, placeholder: "", width: 50)
                .focused($focus, equals: .officeCapacity)
                .keyboardType(.numberPad)
            VStack {
                Button {
                    send(.onAddButtonTapped)
                } label: {
                    Image(resource: \.ic_round_plus)
                        .resizable()
                        .renderingMode(.template)
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 20, height: 20)
                        .padding(.bottom, 4)

                }
                .frame(maxHeight: .infinity)
                .tint(Color(resource: \.textBlack))
                .disabled(store.isAddButtonDisabled)
            }
        }
        .frame(height: 42)

    }

    @ViewBuilder
    func menuButton(_ text: String, image: Image) -> some View {
        HStack {
            image
                .resizable()
                .frame(width: 20, height: 20)
                .foregroundStyle(Color(resource: \.textBlack))
            Text(text)
                .font(Font(resource: \.montserrat_medium, size: 12))
                .foregroundStyle(Color(resource: \.textBlack))
        }
    }

    @ViewBuilder
    func borderedText(_ text: String, maxWidth: CGFloat, alignment: Alignment = .center) -> some View {
        styledText(text)
            .frame(maxWidth: maxWidth, alignment: alignment)
            .overlay {
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(resource: \.textLight), lineWidth: 1)
            }
    }

    @ViewBuilder
    func borderedText(_ text: String, width: CGFloat) -> some View {
        styledText(text)
            .frame(width: width)
            .overlay {
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(resource: \.textLight), lineWidth: 1)
            }
    }

    @ViewBuilder
    func styledText(_ text: String) -> some View {
        Text(text)
            .font(Font(resource: \.montserrat_medium, size: 12))
            .foregroundStyle(Color(resource: \.textBlack))
            .lineLimit(1)
            .padding(14)
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
    func borderedTextField(_ text: Binding<String>, placeholder: String, width: CGFloat, textAlignment: TextAlignment = .center) -> some View {
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
            .multilineTextAlignment(textAlignment)
            .font(Font(resource: \.montserrat_medium, size: 12))
            .foregroundStyle(Color(resource: \.textBlack))
            .lineLimit(1)
            .padding(14)
    }
}
