import SwiftUI
import AppFeature
import FirebaseCore

@main
struct iOSApp: App { // swiftlint:disable:this type_name

    let env = Bundle.main.object(forInfoDictionaryKey: "ENV") as? String

    init() {
        FirebaseApp.configure()
    }
	var body: some Scene {
		WindowGroup {
            AppView(environment: env)
		}
	}
}
