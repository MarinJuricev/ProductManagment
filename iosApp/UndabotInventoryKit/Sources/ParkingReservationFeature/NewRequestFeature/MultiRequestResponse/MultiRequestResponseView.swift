import ComposableArchitecture
import SwiftUI
import Shared
import Utilities

@ViewAction(for: MultiRequestResponseFeature.self)
public struct MultiRequestResponseView: View {
    public let store: StoreOf<MultiRequestResponseFeature>

    public var body: some View {
        VStack(spacing: 30) {
            HStack {
                Text(resource: \.parking_reservation_multi_request_response_date)
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .foregroundStyle(Color(resource: \.secondary))
                Spacer()
                Text(resource: \.parking_reservation_multi_request_response_status)
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .foregroundStyle(Color(resource: \.secondary))
            }
            .padding(.bottom, 4)
            ForEach(store.dateResponses) { dateResponse in
                HStack {
                    date(dateResponse)
                    Spacer()

                    switch dateResponse.state {
                    case .loading:
                        ProgressView()
                            .progressViewStyle(.circular)
                    case .failure:
                        Image(resource: \.failure)
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 18, height: 18)
                            .foregroundStyle(Color(resource: \.error))
                    case .success:
                        Image(resource: \.ic_tick)
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 18, height: 18)
                            .foregroundStyle(Color(resource: \.success))
                    }
                }
            }
            Spacer()

            Button {
                send(.onDoneButtonTapped)
            } label: {
                Text(resource: \.parking_reservation_new_multi_requests_done)
                    .padding(8)
                    .font(Font(resource: \.montserrat_semibold, size: 12))
                    .foregroundStyle(Color(resource: \.surface))
                    .frame(maxWidth: .infinity)
            }
            .disabled(store.isDoneButtonDisabled)
            .tint(Color(resource: \.secondary))
            .buttonStyle(.borderedProminent)
            .buttonBorderShape(.roundedRectangle(radius: 12))
            .padding(.bottom, 20)

        }
        .padding(21)
        .task {
            send(.onTask)
        }
    }

    @ViewBuilder
    func date(_ dateResponse: DateResponse) -> some View {
        VStack(alignment: .leading) {
            Text(dateResponse.date.format(using: .numericDottedFormatter))
                .font(Font(resource: \.montserrat_medium, size: 12))
                .foregroundStyle(Color(resource: \.textBlack))
                .padding(8)
                .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .strokeBorder(
                                style: StrokeStyle(lineWidth: 0.5)
                            )
                            .foregroundColor(dateResponse.state == .failure ? Color(resource: \.error) : Color(resource: \.textLight))
                    )
        }
    }
}
