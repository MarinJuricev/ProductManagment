import ComposableArchitecture
import SwiftUI

@ViewAction(for: MultiDatePickerFeature.self)
public struct MultiDatePickerView: View {
    @Bindable public var store: StoreOf<MultiDatePickerFeature>
    private let flexibleColumn = [
        GridItem(.flexible(minimum: 50, maximum: 170)),
        GridItem(.flexible(minimum: 50, maximum: 170)),
        GridItem(.flexible(minimum: 50, maximum: 170))
    ]

    public init(store: StoreOf<MultiDatePickerFeature>) {
        self.store = store
    }

    public var body: some View {
        Group {
            switch store.alreadyReservedDates {
            case .initial:
                EmptyView()
            case .loading:
                ProgressView()
                    .progressViewStyle(.circular)
            case .loaded:
                LazyVGrid(columns: flexibleColumn) {
                    if store.shouldShowAddButton {
                        VStack {
                            Spacer()
                            Button {
                                send(.onAddButtonTapped)
                            } label: {
                                Text(resource: \.parking_reservation_new_request_add_new_date)
                                    .lineLimit(1)
                                    .font(Font(resource: \.montserrat_semibold, size: 10))
                                    .foregroundStyle(Color(resource: \.surface))
                            }
                            .tint(Color(resource: \.secondary))
                            .buttonStyle(.borderedProminent)
                            .buttonBorderShape(.roundedRectangle(radius: 23))
                            .frame(height: 30)
                        }
                    }
                    ForEach(store.dates, id: \.self) { dateComponent in
                        let date = Calendar.current.date(from: dateComponent)?.format(using: .numericDottedFormatter) ?? ""
                        VStack(alignment: .trailing, spacing: 4) {
                            Button {
                                send(.onRemoveButtonTapped(dateComponent), animation: .easeInOut)
                            } label: {
                                Image(resource: \.close_round)
                                    .resizable()
                                    .frame(width: 15, height: 15)
                                    .foregroundStyle(Color(resource: \.textLight))
                            }
                            Text(date)
                                .lineLimit(1)
                                .font(Font(resource: \.montserrat_semibold, size: 11))
                                .foregroundStyle(Color(resource: \.secondary))
                                .padding(.horizontal, 6)
                                .frame(height: 28)
                                .overlay {
                                    RoundedRectangle(cornerRadius: 23)
                                        .stroke(Color(resource: \.secondary), lineWidth: 1)
                                }
                                .padding(.bottom, 2)
                        }
                    }

                }
                .animation(.easeInOut, value: store.dates)
                .calendarView(isPresented: $store.isCalendarPresented, dates: $store.dates, startDate: store.startDate, endDate: store.endDate, excludedDates: store.excludedDates)
            case .failed:
                Button(action: {
                    send(.onRetryButtonTapped)
                }, label: {
                    Text(resource: \.general_retry_button_text)
                })
            }
        }
        .task {
            send(.onTask)
        }
    }
}
