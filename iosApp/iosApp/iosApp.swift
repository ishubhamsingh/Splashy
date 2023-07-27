import UIKit
import SwiftUI
import ComposeApp

@main
struct iosApp: App {

init() {
		LoggingKt.initialiseLogging()
	}
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.keyboard)
    }
}

struct ComposeView: UIViewControllerRepresentable {
	let lifecycle: LifecycleRegistry = LifecycleRegistryKt.LifecycleRegistry()

	init() {
		LifecycleRegistryExtKt.create(lifecycle)
	}
    func makeUIViewController(context: Context) -> UIViewController {
        let applicationComponent = InjectApplicationComponent(componentContext: DefaultComponentContext(lifecycle: lifecycle))
        let unsplashApi = applicationComponent.unsplashApiCreator()
        let controller = MainKt.MainViewController(unsplashApi: unsplashApi)
        LifecycleRegistryExtKt.resume(lifecycle)
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
