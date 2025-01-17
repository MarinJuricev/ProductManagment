import SwiftUI
import UIKit

public struct CalendarViewModifier: ViewModifier {
    @Binding var isPresented: Bool
    @Binding var dates: [DateComponents]
    let startDate: Date
    let endDate: Date
    let excludedDates: [Date]

    public func body(content: Content) -> some View {
        content
            .popover(isPresented: $isPresented) {
                CalendarView(availableDateRange: .init(start: startDate, end: endDate), selection: $dates)
                    .selectable { dateComponents in
                        guard let date = dateComponents.date else {
                            return false
                        }
                        return !excludedDates.contains { $0.startOfDay() == date.startOfDay() }
                    }
                    .deselectable(!dates.isEmpty)
                    .frame(width: 320, height: 320)
                    .scaleEffect(0.95)
                    .presentationCompactAdaptation(.popover)
                    .presentationBackgroundInteraction(.disabled)
                    .presentationBackground(Color.clear)
            }
    }
}

public extension View {
    func calendarView(isPresented: Binding<Bool>, dates: Binding<[DateComponents]>, startDate: Date, endDate: Date, excludedDates: [Date]) -> some View {
        self.modifier(CalendarViewModifier(isPresented: isPresented, dates: dates, startDate: startDate, endDate: endDate, excludedDates: excludedDates))
    }
}

public struct CalendarView: UIViewRepresentable {
    struct Decoration {
        var systemImage: String?
        var image: String?
        var color: Color?
        var size: UICalendarView.DecorationSize?
        var customView: ((DateComponents) -> AnyView)?
    }

    @Environment(\.calendar) private var calendar
    @Environment(\.locale) private var locale
    @Environment(\.timeZone) private var timeZone

    private let availableDateRange: DateInterval
    private let visibleDateComponents: Binding<DateComponents>?
    private var selection: Binding<DateComponents?>?
    private var selections: Binding<[DateComponents]>?

    private var fontDesign = UIFontDescriptor.SystemDesign.default
    private var canSelectDate: ((DateComponents) -> Bool)?
    private var selectableChangeValue: (any Equatable)?
    private var canDeselectDate: ((DateComponents) -> Bool)?
    private var decorations = [DateComponents: Decoration]()

    // MARK: Initializers

    public init(availableDateRange: DateInterval = .init(start: .distantPast, end: .distantFuture)) {
        self.availableDateRange = availableDateRange
        self.visibleDateComponents = nil
        self.selection = nil
    }

    public init(availableDateRange: DateInterval = .init(start: .distantPast, end: .distantFuture), visibleDateComponents: Binding<DateComponents>) {
        self.availableDateRange = availableDateRange
        self.visibleDateComponents = visibleDateComponents
        self.selection = nil
    }

    public init(availableDateRange: DateInterval = .init(start: .distantPast, end: .distantFuture), selection: Binding<DateComponents?>) {
        self.availableDateRange = availableDateRange
        self.visibleDateComponents = nil
        self.selection = selection
        self.selections = nil
    }

    public init(availableDateRange: DateInterval = .init(start: .distantPast, end: .distantFuture), visibleDateComponents: Binding<DateComponents>, selection: Binding<DateComponents?>) {
        self.availableDateRange = availableDateRange
        self.visibleDateComponents = visibleDateComponents
        self.selection = selection
        self.selections = nil
    }

    public init(availableDateRange: DateInterval = .init(start: .distantPast, end: .distantFuture), selection: Binding<[DateComponents]>) {
        self.availableDateRange = availableDateRange
        self.visibleDateComponents = nil
        self.selections = selection
        self.selection = nil
    }

    public init(availableDateRange: DateInterval = .init(start: .distantPast, end: .distantFuture), visibleDateComponents: Binding<DateComponents>, selection: Binding<[DateComponents]>) {
        self.availableDateRange = availableDateRange
        self.visibleDateComponents = visibleDateComponents
        self.selections = selection
        self.selection = nil
    }

    // MARK: - UIViewRepresentable

    public func makeUIView(context: Context) -> UICalendarView {
        let calendarView = UICalendarView()
        calendarView.delegate = context.coordinator

        // must use low compression resistance for horizontal padding and frame modifiers to work properly
        calendarView.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)

        return calendarView
    }

    public func updateUIView(_ calendarView: UICalendarView, context: Context) {
        context.coordinator.parent = self

        context.coordinator.isUpdatingView = true
        defer { context.coordinator.isUpdatingView = false }

        calendarView.calendar = calendar
        calendarView.locale = locale
        calendarView.timeZone = timeZone
        calendarView.availableDateRange = availableDateRange
        calendarView.fontDesign = fontDesign

        let canAnimate = context.transaction.animation != nil

        // visible date components

        if let binding = visibleDateComponents {
            let visibleYearMonth = calendarView.visibleDateComponents.yearMonth
            let newYearMonth = binding.wrappedValue.yearMonth

            if newYearMonth != visibleYearMonth {
                calendarView.setVisibleDateComponents(newYearMonth, animated: canAnimate || binding.canAnimate)
            }
        }

        // decorations

        calendarView.reloadDecorationsForVisibleMonth(animated: canAnimate)

        // selection

        if let selection {
            if let dateSelection = calendarView.selectionBehavior as? UICalendarSelectionSingleDate {
                if dateSelection.selectedDate != selection.wrappedValue {
                    dateSelection.setSelected(selection.wrappedValue, animated: canAnimate || selection.canAnimate)
                }

                dateSelection.updateSelectableDates()
            } else {
                let dateSelection = UICalendarSelectionSingleDate(delegate: context.coordinator)
                calendarView.selectionBehavior = dateSelection
                dateSelection.setSelected(selection.wrappedValue, animated: canAnimate || selection.canAnimate)
            }
        } else if let selections {
            if let dateSelections = calendarView.selectionBehavior as? UICalendarSelectionMultiDate {
                if dateSelections.selectedDates != selections.wrappedValue {
                    dateSelections.setSelectedDates(selections.wrappedValue, animated: canAnimate || selections.canAnimate)
                }

                dateSelections.updateSelectableDates()
            } else {
                let dateSelections = UICalendarSelectionMultiDate(delegate: context.coordinator)
                calendarView.selectionBehavior = dateSelections
                dateSelections.setSelectedDates(selections.wrappedValue, animated: canAnimate || selections.canAnimate)
            }
        } else {
            // setting selectionBehavior reloads the view which can interfere
            // with animations and scrolling, so only set if actually changed
            if calendarView.selectionBehavior != nil {
                calendarView.selectionBehavior = nil
            }
        }
    }

    public class Coordinator: NSObject {
        var parent: CalendarView
        var isUpdatingView = false

        init(_ parent: CalendarView) {
            self.parent = parent
        }
    }

    public func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
}

// MARK: - Font Design

public extension CalendarView {
    func fontDesign(_ design: Font.Design) -> Self {
        var view = self

        switch design {
        case .default:
            view.fontDesign = .default

        case .serif:
            view.fontDesign = .serif

        case .rounded:
            view.fontDesign = .rounded

        case .monospaced:
            view.fontDesign = .monospaced

        @unknown default:
            view.fontDesign = .default
        }

        return view
    }
}

// MARK: - Decorations

public extension CalendarView {
    func decorating(_ dateComponents: Set<DateComponents>, systemImage: String? = nil, color: Color? = nil, size: UICalendarView.DecorationSize = .medium) -> Self {
        var view = self
        view.add(Decoration(systemImage: systemImage, color: color, size: size), for: dateComponents)
        return view
    }

    func decorating(_ dateComponents: Set<DateComponents>, image: String, color: Color? = nil, size: UICalendarView.DecorationSize = .medium) -> Self {
        var view = self
        view.add(Decoration(image: image, color: color, size: size), for: dateComponents)
        return view
    }

    func decorating(_ dateComponents: Set<DateComponents>, customView: @escaping () -> some View) -> Self {
        var view = self
        view.add(Decoration(customView: { _ in AnyView(customView()) }), for: dateComponents)
        return view
    }

    func decorating(_ dateComponents: Set<DateComponents>, customView: @escaping (DateComponents) -> some View) -> Self {
        var view = self
        view.add(Decoration(customView: { AnyView(customView($0)) }), for: dateComponents)
        return view
    }

    private mutating func add(_ decoration: Decoration, for dateComponents: Set<DateComponents>) {
        for key in dateComponents.compactMap(\.decorationKey) {
            decorations[key] = decoration
        }
    }
}

// MARK: - Selections

public extension CalendarView {
    func selectable(updatingOnChangeOf value: (any Equatable)? = nil, canSelectDate: @escaping (DateComponents) -> Bool) -> Self {
        var view = self
        view.canSelectDate = canSelectDate
        view.selectableChangeValue = value
        return view
    }

    func deselectable(canDeselectDate: @escaping (DateComponents) -> Bool) -> Self {
        var view = self
        view.canDeselectDate = canDeselectDate
        return view
    }

    func deselectable(_ canDeselectDates: Bool = true) -> Self {
        deselectable { _ in canDeselectDates }
    }
}

// MARK: - UICalendarViewDelegate

extension CalendarView.Coordinator: UICalendarViewDelegate {
    public func calendarView(_ calendarView: UICalendarView, didChangeVisibleDateComponentsFrom previousDateComponents: DateComponents) {
        parent.visibleDateComponents?.wrappedValue = calendarView.visibleDateComponents
    }
}

// MARK: - UICalendarSelectionSingleDateDelegate

extension CalendarView.Coordinator: UICalendarSelectionSingleDateDelegate {
    public func dateSelection(_ selection: UICalendarSelectionSingleDate, canSelectDate dateComponents: DateComponents?) -> Bool {
        if let dateComponents {
            return parent.canSelectDate?(dateComponents) ?? true
        }

        if let canDeselectDate = parent.canDeselectDate, let selectedDate = selection.selectedDate {
            return canDeselectDate(selectedDate)
        }

        return false // UICalendarView's default behavior
    }

    public func dateSelection(_ selection: UICalendarSelectionSingleDate, didSelectDate dateComponents: DateComponents?) {
        parent.selection?.wrappedValue = dateComponents
    }
}

// MARK: - UICalendarSelectionMultiDateDelegate

extension CalendarView.Coordinator: UICalendarSelectionMultiDateDelegate {
    public func multiDateSelection(_ selection: UICalendarSelectionMultiDate, canSelectDate dateComponents: DateComponents) -> Bool {
        parent.canSelectDate?(dateComponents) ?? true
    }

    public func multiDateSelection(_ selection: UICalendarSelectionMultiDate, canDeselectDate dateComponents: DateComponents) -> Bool {
        parent.canDeselectDate?(dateComponents) ?? true
    }

    public func multiDateSelection(_ selection: UICalendarSelectionMultiDate, didSelectDate dateComponents: DateComponents) {
        parent.selections?.wrappedValue = selection.selectedDates
    }

    public func multiDateSelection(_ selection: UICalendarSelectionMultiDate, didDeselectDate dateComponents: DateComponents) {
        parent.selections?.wrappedValue = selection.selectedDates
    }
}

// MARK: - Helper

private extension [DateComponents: CalendarView.Decoration] {
    func decorationFor(year: Int?, month: Int?, day: Int?) -> Value? {
        self[.init(year: year, month: month, day: day)] ??
        self[.init(month: month, day: day)] ??
        self[.init(day: day)]
    }
}

private extension DateComponents {
    var yearMonth: DateComponents {
        DateComponents(year: year, month: month)
    }

    var decorationKey: DateComponents? {
        guard let day else {
            return nil
        }

        if let year, let month {
            return .init(year: year, month: month, day: day)
        }

        if let month {
            return .init(month: month, day: day)
        }

        return .init(day: day)
    }
}

private extension Binding {
    var canAnimate: Bool {
        transaction.animation != nil
    }
}

extension UICalendarView {
    func reloadDecorationsForVisibleMonth(animated: Bool) {
        guard let visibleMonth = calendar.date(from: visibleDateComponents) else {
            return
        }

        var daysToReload = [DateComponents]()

        for day in calendar.range(of: .day, in: .month, for: visibleMonth)! {
            var components = visibleDateComponents
            components.day = day
            daysToReload.append(components)
        }

        reloadDecorations(forDateComponents: daysToReload, animated: animated)
    }
}
