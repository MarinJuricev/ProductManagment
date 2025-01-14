import SwiftUI

// Wrapper for using navigationDestination(item:) on iOS 16
public extension View {
  @available(iOS, introduced: 13, deprecated: 17)
  @available(macOS, introduced: 10.15, deprecated: 13)
  @available(tvOS, introduced: 13, deprecated: 16)
  @available(watchOS, introduced: 6, deprecated: 9)
  @ViewBuilder
  func navigationDestinationWrapper<D: Hashable, C: View>(
    item: Binding<D?>,
    @ViewBuilder destination: @escaping (D) -> C
  ) -> some View {
    if #available(iOS 17, macOS 14, tvOS 17, visionOS 1, watchOS 10, *) {
      navigationDestination(item: item, destination: destination)
    } else {
      navigationDestination(
        isPresented: Binding(
          get: { item.wrappedValue != nil },
          set: { isPresented, transaction in
            if !isPresented {
              item.transaction(transaction).wrappedValue = nil
            }
          }
        )
      ) {
        if let item = item.wrappedValue {
          destination(item)
        }
      }
    }
  }
}
