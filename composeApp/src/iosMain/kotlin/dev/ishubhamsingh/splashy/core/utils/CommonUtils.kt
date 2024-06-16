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

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.Font
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform as NativePlatform
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSISO8601DateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.autoupdatingCurrentLocale
import platform.Foundation.localTimeZone

actual fun getHttpClient(): HttpClient {
  val httpClient =
    HttpClient(Darwin) {
      engine {
        configureRequest {
          setTimeoutInterval(5 * 60.0)
          setAllowsCellularAccess(true)
        }
      }
    }

  return httpClient
}

private val cache: MutableMap<String, Font> = mutableMapOf()

actual fun getFormattedDateTime(timestamp: String, format: String): String {
  val date = NSISO8601DateFormatter().dateFromString(timestamp) ?: return ""

  val dateFormatter = NSDateFormatter()
  dateFormatter.timeZone = NSTimeZone.localTimeZone
  dateFormatter.locale = NSLocale.autoupdatingCurrentLocale
  dateFormatter.dateFormat = format
  return dateFormatter.stringFromDate(date)
}

actual fun getPlatform(): Platform = Platform.iOS

@OptIn(ExperimentalNativeApi::class) actual fun isDebug(): Boolean = NativePlatform.isDebugBinary

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeight(): Int {
  return LocalWindowInfo.current.containerSize.height
}

@Composable
actual fun UpdateSystemBars(
  statusBarColor: Color,
  navigationBarColor: Color,
  isDarkTheme: Boolean
) {}
