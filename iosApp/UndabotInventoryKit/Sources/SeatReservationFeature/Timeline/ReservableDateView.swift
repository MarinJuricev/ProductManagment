import ComposableArchitecture
import SwiftUI
import CommonUI
import Utilities
import Core
import Shared

@ViewAction(for: ReservableDateFeature.self)
public struct ReservableDateView: View {
    public var store: StoreOf<ReservableDateFeature>

    public init(
        store: StoreOf<ReservableDateFeature>
    ) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            switch store.reservableDate {
            case .weekend(date: let date):
                ReservableDateContent(date: date.format(using: .numericDottedFormatter), dateColor: Color(resource: \.disabled), textColor: Color(resource: \.textLight)) {
                    ZStack {
                        Image(resource: \.weekend_icon)
                            .resizable()
                            .frame(width: 180, height: 180)
                            .aspectRatio(contentMode: .fit)
                    }
                } button: {
                    Text(resource: \.seat_reservation_weekend)
                        .font(Font(resource: \.montserrat_semibold, size: 14))
                        .foregroundStyle(Color(resource: \.textLight))
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color(resource: \.textLight).opacity(0.2))
                }
                .padding(.horizontal, 20)

            case let .weekday(date: date, seatHolders: seatHolders, remainingSeats: remainingSeats, isFullyReserved: isFullyReserved):
                ReservableDateContent(date: date.format(using: .numericDottedFormatter), dateColor: Color(resource: \.secondary), textColor: Color(resource: \.surface)) {
                    VStack {
                        remainingSeatsText(remainingSeats)
                            .padding(.top, 25)
                            .padding(.bottom, 5)
                        ScrollView {
                            VStack(alignment: .leading) {
                                ForEach(seatHolders) { seatHolder in
                                    HStack {
                                        CircularImage(url: seatHolder.profileImageUrl, diameter: 24)
                                        Text(seatHolder.email.username)
                                            .layoutPriority(10)
                                            .lineLimit(1)
                                            .font(Font(resource: \.montserrat_regular, size: 14))
                                            .foregroundStyle(Color(resource: \.textBlack))
                                    }
                                    .padding(4)
                                    .padding(.trailing, 2)
                                    .overlay {
                                        RoundedRectangle(cornerRadius: 15)
                                            .stroke(Color(resource: \.textLight), lineWidth: 1)
                                    }
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(15)

                        }
                    }
                } button: {
                    actionButton(isFullyReserved: isFullyReserved)
                }
                .padding(.horizontal, 20)
            }
        }
    }

    @ViewBuilder
    func actionButton(isFullyReserved: Bool) -> some View {
        ZStack {
            Group {
                if store.isAlreadyReserved {
                    cancelReservationButton
                } else {
                    reserveButton
                        .disabled(isFullyReserved)
                        .opacity(isFullyReserved ? 0.5 : 1.0)
                }
            }
            .disabled(store.isRequestInFlight)
            .opacity(store.isRequestInFlight ? 0.0 : 1.0)
            if store.isRequestInFlight {
                ProgressView()
                    .progressViewStyle(.circular)
            }
        }
    }

    @ViewBuilder
    var reserveButton: some View {
        Button(action: {
            send(.reserveButtonTapped)
        }, label: {
            Text(resource: \.seat_reservation_reserve_seat_button)
                .font(Font(resource: \.montserrat_semibold, size: 14))
                .foregroundStyle(Color.white)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color(resource: \.secondary))
        })
    }

    @ViewBuilder
    var cancelReservationButton: some View {
        Button(action: {
            send(.cancelReservationButton)
        }, label: {
            Text(resource: \.seat_reservation_cancel_reservation_button)
                .font(Font(resource: \.montserrat_semibold, size: 14))
                .foregroundStyle(Color(resource: \.textBlack))
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color(resource: \.disabled))
        })
    }

    @ViewBuilder
    func remainingSeatsText(_ remainingSeats: Int) -> some View {
        let text = seatReservationRemainingSeats(quantity: Int32(remainingSeats)).localized()
        if let firstBlankSpaceIndex = text.firstIndex(of: " ") {
            let boldText = String(text[..<firstBlankSpaceIndex])
            let regularText = String(text[firstBlankSpaceIndex...])
            HStack(spacing: 0) {
                Text(boldText)
                    .font(Font(resource: \.montserrat_semibold, size: 16))
                    .foregroundStyle(Color(resource: \.secondary))
                Text(regularText)
                    .font(Font(resource: \.montserrat_regular, size: 16))
                    .foregroundStyle(Color(resource: \.textBlack))
            }
        }
    }
}

struct ReservableDateContent<Content: View, Button: View>: View {
    let date: String
    let dateColor: Color
    let textColor: Color
    @ViewBuilder let content: Content
    @ViewBuilder let button: Button

    var body: some View {
        VStack(spacing: 0) {
            Text(date)
                .font(Font(resource: \.montserrat_semibold, size: 16))
                .foregroundStyle(textColor)
                .frame(width: 220, height: 47)
                .background(dateColor)
                .clipShape(.rect(topLeadingRadius: 18,
                                 topTrailingRadius: 18))
            VStack(spacing: 0) {
                content
                    .frame(height: 300)
                button
                    .frame(height: 50)
            }
            .background(Color.white)
            .clipShape(.rect(cornerRadius: 23))
            .overlay {
                RoundedRectangle(cornerRadius: 23)
                    .stroke(Color(resource: \.textLight), lineWidth: 1.0)
            }

        }
    }
}
