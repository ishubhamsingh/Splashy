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
package dev.ishubhamsingh.splashy.core.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

actual fun getHttpClient(): HttpClient {
  val httpClient =
    HttpClient(OkHttp) {
      engine {
        config {
          retryOnConnectionFailure(true)
          callTimeout(Duration.ofMinutes(0))
          connectTimeout(Duration.ofMinutes(0))
          readTimeout(Duration.ofMinutes(0))
          writeTimeout(Duration.ofMinutes(0))
        }
      }
    }

  return httpClient
}

@SuppressLint("DiscouragedApi")
@Composable
actual fun font(name: String, res: String, weight: FontWeight, style: FontStyle): Font {
  val context = LocalContext.current
  val id = context.resources.getIdentifier(res, "font", context.packageName)
  return Font(id, weight, style)
}

actual fun getFormattedDateTime(timestamp: String, format: String): String {
  val date = ZonedDateTime.parse(timestamp)
  val formatter = DateTimeFormatter.ofPattern(format)
  return date.format(formatter)
}
