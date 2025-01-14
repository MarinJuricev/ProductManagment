import SwiftUI

public enum RangeType {
    case closed(ClosedRange<Date>)
    case partialThrough(PartialRangeThrough<Date>)
    case partialFrom(PartialRangeFrom<Date>)
}

extension View {
    public func datePicker(isPresented: Binding<Bool>, date: Binding<Date>, range: RangeType? = nil) -> some View {
        modifier(DatePickerModifier(isPresented: isPresented, date: date, range: range))
    }
}

public struct DatePickerModifier: ViewModifier {
    @Binding var isPresented: Bool
    @Binding var date: Date
    private let range: RangeType?

    public init(isPresented: Binding<Bool>, date: Binding<Date>, range: RangeType?) {
        self._isPresented = isPresented
        self._date = date
        self.range = range
    }

    public func body(content: Content) -> some View {
        content
            .popover(isPresented: $isPresented) {
                datePicker
                    .scaleEffect(0.95)
                    .frame(width: 320, height: 320)
                    .datePickerStyle(.graphical)
                    .presentationCompactAdaptation(.popover)
                    .presentationBackgroundInteraction(.disabled)
                    .presentationBackground(Color.clear)
            }

    }

    @ViewBuilder
    var datePicker: some View {
        switch range {
        case .closed(let closedRange):
            DatePicker("", selection: $date, in: closedRange, displayedComponents: .date)
        case .partialThrough(let partialRangeThrough):
            DatePicker("", selection: $date, in: partialRangeThrough, displayedComponents: .date)
        case .partialFrom(let partialRangeFrom):
            DatePicker("", selection: $date, in: partialRangeFrom, displayedComponents: .date)
        case .none:
            DatePicker("", selection: $date, displayedComponents: .date)
        }
    }
}
