/*
 * Copyright 2023 Shubham Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ishubhamsingh.splashy

import android.app.Application
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

  return httpClient
}
