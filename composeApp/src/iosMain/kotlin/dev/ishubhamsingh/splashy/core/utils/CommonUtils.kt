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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
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

@OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)
@Composable
actual fun font(name: String, res: String, weight: FontWeight, style: FontStyle): Font {
  return cache.getOrPut(res) {
    val byteArray = runBlocking { resource("font/$res.ttf").readBytes() }
    androidx.compose.ui.text.platform.Font(res, byteArray, weight, style)
  }
}

actual fun getFormattedDateTime(timestamp: String, format: String): String {
  val date = NSISO8601DateFormatter().dateFromString(timestamp) ?: return ""

  val dateFormatter = NSDateFormatter()
  dateFormatter.timeZone = NSTimeZone.localTimeZone
  dateFormatter.locale = NSLocale.autoupdatingCurrentLocale
  dateFormatter.dateFormat = format
  return dateFormatter.stringFromDate(date)
}

actual fun getPlatform(): Platform = Platform.iOS
