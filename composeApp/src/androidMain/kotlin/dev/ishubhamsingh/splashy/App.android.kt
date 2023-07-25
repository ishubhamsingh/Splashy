package dev.ishubhamsingh.splashy

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import dev.ishubhamsingh.splashy.core.utils.initialiseLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.time.Duration

class SplashyApp : Application() {
    companion object {
        lateinit var INSTANCE: SplashyApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initialiseLogging()
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

internal actual fun getPlatform(): String = "Android"

@RequiresApi(Build.VERSION_CODES.O)
actual fun getHttpClient(): HttpClient {
    val httpClient =
        HttpClient(OkHttp) {
            engine {
                config {
                    retryOnConnectionFailure(true)
                    callTimeout(Duration.ofMinutes(2))
                }
            }
        }

    return  httpClient
}