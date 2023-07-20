import androidx.compose.ui.window.ComposeUIViewController
import dev.ishubhamsingh.splashy.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { App() }
}
