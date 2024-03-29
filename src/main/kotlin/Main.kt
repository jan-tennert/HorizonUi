import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import io.github.jan.horizon.di.appModule
import io.github.jan.horizon.ui.screen.AppScreen
import io.github.jan.horizon.vm.AppViewModel
import org.koin.core.context.startKoin

@Composable
@Preview
fun FrameWindowScope.Root() {
    val appViewModel = remember { AppViewModel() }
    MaterialTheme(darkColorScheme()) {
        Surface(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AppScreen(appViewModel)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            appViewModel.dispose()
        }
    }
}

fun main() {
    startKoin {
        modules(appModule)
    }
    application {
        Window(onCloseRequest = ::exitApplication, title = "Horizon Ui", state = rememberWindowState(size = DpSize(1280.dp, 720.dp))) {
            Root()
        }
    }
}
