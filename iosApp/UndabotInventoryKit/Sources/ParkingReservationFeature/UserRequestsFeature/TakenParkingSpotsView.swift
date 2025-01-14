import SwiftUI
import Shared
import Utilities

struct TakenParkingSpotsView: View {
    var body: some View {
        HStack {
            Text(resource: \.parking_reservation_no_free_spaces_error)
                .foregroundStyle(Color(resource: \.warning))
                .font(Font(resource: \.montserrat_semibold, size: 12))
        }
        .frame(maxWidth: .infinity)
        .frame(height: 52)
        .background(Color(resource: \.warning).opacity(0.08))
        .clipShape(.rect(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .strokeBorder(
                    style: StrokeStyle(lineWidth: 0.5)
                )
                .foregroundColor(Color(resource: \.warning).opacity(0.46))
        )
    }
}
