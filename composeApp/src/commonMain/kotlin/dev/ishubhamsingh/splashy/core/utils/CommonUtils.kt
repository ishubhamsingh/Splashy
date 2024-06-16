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
import androidx.compose.ui.graphics.Color
import io.ktor.client.HttpClient

expect fun getHttpClient(): HttpClient

expect fun getFormattedDateTime(timestamp: String, format: String = "dd MMM yyyy, hh:mm a"): String

expect fun getPlatform(): Platform

expect fun isDebug(): Boolean

@Composable expect fun getScreenHeight(): Int

enum class Platform {
  Android,
  iOS
}

@Composable
expect fun UpdateSystemBars(statusBarColor: Color, navigationBarColor: Color, isDarkTheme: Boolean)
