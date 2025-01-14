import SwiftUI
import Foundation
import Shared
import Utilities
import Core

public struct MyReservationItemView: View {
    private let submittedDate: Date
    private let requestedDate: Date
    private let status: IAParkingReservationStatus

    public var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 16) {
                Text(resource: \.my_parking_reservation_submitted)
                    .foregroundStyle(Color(resource: \.secondary))
                Text(submittedDate.format(using: .numericDottedFormatter))
                    .foregroundStyle(Color(resource: \.textBlack))
            }
            VStack(alignment: .leading, spacing: 16) {
                Text(resource: \.parking_reservation_item_requested_date)
                    .foregroundStyle(Color(resource: \.secondary))
                Text(requestedDate.format(using: .numericDottedFormatter))
                    .foregroundStyle(Color(resource: \.textBlack))
            }
            Spacer()
            Text(status.title)
                .foregroundStyle(Color(resource: \.surface))
                .frame(maxWidth: 90, maxHeight: 30)
                .background(status.color)
                .containerShape(.rect(cornerRadius: 9))
        }
        .font(Font(resource: \.montserrat_semibold, size: 12))
        .padding(.vertical, 12)
        .padding(.horizontal, 16)
        .background(Color(resource: \.surface))
        .containerShape(.rect(cornerRadius: 23))

    }
}

extension MyReservationItemView {
    public init(with parkingReservation: IAParkingReservation) {
        self.submittedDate = parkingReservation.createdAt
        self.requestedDate = parkingReservation.date
        self.status = parkingReservation.status
    }
}
