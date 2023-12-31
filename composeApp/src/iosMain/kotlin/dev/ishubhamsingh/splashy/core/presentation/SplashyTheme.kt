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
package dev.ishubhamsingh.splashy.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.ishubhamsingh.splashy.ui.theme.DarkColors
import dev.ishubhamsingh.splashy.ui.theme.LightColors
import dev.ishubhamsingh.splashy.ui.theme.Shapes
import dev.ishubhamsingh.splashy.ui.theme.getTypography

@Composable
actual fun SplashyTheme(
  darkTheme: Boolean,
  dynamicColor: Boolean,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = getTypography(),
    shapes = Shapes,
    content = content
  )
}
