import ComposableArchitecture
import SwiftUI
import CommonUI
import Shared
import Utilities
import CasePaths

@ViewAction(for: MyReservationDetailsFeature.self)
public struct MyReservationDetailsView: View {
    @Bindable public var store: StoreOf<MyReservationDetailsFeature>

    public init(store: StoreOf<MyReservationDetailsFeature>) {
        self.store = store
    }

    public var body: some View {
        WithPerceptionTracking {
            ZStack {
                VStack(spacing: 20) {
                    Text(store.parkingReservation.status.title)
                        .foregroundStyle(Color(resource: \.surface))
                        .frame(maxWidth: 90, maxHeight: 30)
                        .background(store.parkingReservation.status.color)
                        .containerShape(.rect(cornerRadius: 9))

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

                    if let submissionNote = store.submissionNote, !submissionNote.isEmpty,
                       let adminNoteTitle {
                        VStack(alignment: .leading, spacing: 10) {
                            Text(resource: adminNoteTitle)
                                .font(Font(resource: \.montserrat_semibold, size: 12))
                                .foregroundStyle(Color(resource: \.secondary))

                            PreviewTextField(text: submissionNote)
                        }
                    }
                    parkingCoordinates()
                    cancelButton()
                }
                .padding(20)
                .alert($store.scope(state: \.destination?.alert, action: \.destination.alert))
                if store.isRequestInFlight {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            }
        }
    }

    @ViewBuilder
    func cancelButton() -> some View {
        if store.shouldShowCancelButton {
            Button {
                send(.cancelButtonTapped)
            } label: {
                Text(resource: \.my_parking_reservation_cancel_request)
                    .padding(8)
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .foregroundStyle(Color(resource: \.surface))
                    .frame(maxWidth: .infinity)
            }
            .tint(Color(resource: \.error))
            .buttonStyle(.borderedProminent)
            .buttonBorderShape(.roundedRectangle(radius: 12))
        }
    }

    @ViewBuilder
    func parkingCoordinates() -> some View {
        if let parkingCoordinate = store.parkingCoordinate {
            HStack(spacing: 65) {
                VStack(alignment: .leading, spacing: 16) {
                    Text(resource: \.parking_reservation_item_garage_level)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    BorderedText(parkingCoordinate.level.title)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.textBlack))

                }

                VStack(alignment: .leading, spacing: 16) {
                    Text(resource: \.parking_reservation_item_parking_spot)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.secondary))

                    BorderedText(parkingCoordinate.spot.title)
                        .font(Font(resource: \.montserrat_semibold, size: 12))
                        .foregroundStyle(Color(resource: \.textBlack))
                }
            }
        }
    }

    var adminNoteTitle: KeyPath<MR.strings, StringResource>? {
        switch store.parkingReservation.status {
        case .approved:
            \.parking_reservation_item_approve_note
        case .declined:
            \.parking_reservation_item_reject_reason
        case .cancelled, .submitted:
            nil
        }
    }
}
