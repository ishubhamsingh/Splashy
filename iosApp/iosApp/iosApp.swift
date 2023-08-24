import UIKit
import SwiftUI
import ComposeApp

@main
struct iosApp: App {

init() {
        #if DEBUG
           LoggingKt.initialiseLogging()
        #endif
		DIHelperKt.doInitKoin()
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
    func makeUIViewController(context: Context) -> UIViewController {
        App_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
