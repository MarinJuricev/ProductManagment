import SwiftUI
import Shared

public struct BorderedPickerView<R: PickableItem>: View {
    private var values: [R]
    @Binding private var selectedValue: R?
    private let horizontalPadding: CGFloat

    public init(
        values: [R],
        selectedValue: Binding<R?>,
        horizontalPadding: CGFloat = 0
    ) {
        self.values = values
        self.horizontalPadding = horizontalPadding
        self._selectedValue = selectedValue
    }

    private var valueTitle: String {
        if let selectedValue {
            selectedValue.title
        } else {
            MR.strings().general_minus.desc().localized()
        }
    }

    public var body: some View {
        Menu {
            ForEach(values) { option in
                Button {
                    selectedValue = option
                } label: {
                    PickableItemView(title: option.title, isSelected: selectedValue == option)
                }
            }
        } label: {
            HStack {
                Text(valueTitle)
                    .padding(.horizontal, horizontalPadding)
                    .frame(minWidth: 40)
                Image(resource: \.drop_down_icon)
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundStyle(Color(resource: \.textBlack))
            }
            .padding(.horizontal, 8)
            .frame(height: 45)
            .background(Color(resource: \.surface))
            .clipShape(.rect(cornerRadius: 12))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(
                        style: StrokeStyle(lineWidth: 0.5)
                    )
                    .foregroundColor(Color(resource: \.textLight))
            )
        }
    }
}
