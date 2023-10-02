import UIKit
import SwiftUI
import ComposeApp

@main
struct iosApp: App {

init() {
        LoggingKt.initialiseLogging()
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
        ComposeView().ignoresSafeArea(.all, edges: .vertical)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        App_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
