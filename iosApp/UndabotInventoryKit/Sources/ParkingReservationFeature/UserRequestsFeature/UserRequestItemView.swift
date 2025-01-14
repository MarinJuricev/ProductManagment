import SwiftUI
import Core
import Foundation
import Shared
import Utilities

public struct UserRequestItemView: View {
    private let requestedDate: Date
    private let email: String
    private let status: IAParkingReservationStatus

    public var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 8) {
                HStack(spacing: 8) {
                    Text(resource: \.parking_reservation_item_requested_date)
                        .foregroundStyle(Color(resource: \.secondary))
                    Text(requestedDate.format(using: .numericDottedFormatter))
                        .foregroundStyle(Color(resource: \.textBlack))
                }
                HStack(spacing: 8) {
                    Text(resource: \.parking_reservation_item_email)
                        .foregroundStyle(Color(resource: \.secondary))
                    Text(email)
                        .foregroundStyle(Color(resource: \.textBlack))
                }
            }
            Spacer()
            StatusLabel(title: status.title, backgroundColor: status.color)
        }
        .font(Font(resource: \.montserrat_semibold, size: 12))
        .padding(.vertical, 16)
        .padding(.horizontal, 16)
        .background(Color(resource: \.surface))
        .containerShape(.rect(cornerRadius: 23))
    }
}

extension UserRequestItemView {
    public init(with parkingReservation: IAParkingReservation) {
        self.requestedDate = parkingReservation.date
        self.email = parkingReservation.email
        self.status = parkingReservation.status
    }
}
