import SwiftUI
import Core

public struct OptionView<Option: IADashboardOption>: View {
    private let option: Option
    private let action: () -> Void

    public init(option: Option, action: @escaping () -> Void) {
        self.option = option
        self.action = action
    }

    public var body: some View {
        VStack(spacing: 0) {
            HStack {
                ZStack {
                    Color(resource: \.secondary)
                        .clipShape(.rect(topLeadingRadius: 23, bottomTrailingRadius: 23))
                    if let icon = option.optionIcon {
                        icon
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 41, height: 41)
                    }

                }
                .frame(width: 72)

                Text(option.optionTitle)
                    .padding(.leading, 30)
                    .font(Font(resource: \.montserrat_semibold, size: 20))
                    .foregroundStyle(Color(resource: \.textBlack))
                Spacer()
            }
            .frame(height: 72)
            Text(option.optionDescription)
                .foregroundStyle(Color(resource: \.textBlack))
                .font(Font(resource: \.montserrat_regular, size: 14))
                .multilineTextAlignment(.center)
                .padding(.top, 15)
                .padding(.bottom, 45)
                .padding(.horizontal, 10)
        }
        .background(Color(resource: \.surface))
        .clipShape(.rect(cornerRadius: 23))
        .overlay(RoundedRectangle(cornerRadius: 23)
            .stroke(Color(resource: \.textLight), lineWidth: 1))
        .onTapGesture(perform: action)
    }
}
