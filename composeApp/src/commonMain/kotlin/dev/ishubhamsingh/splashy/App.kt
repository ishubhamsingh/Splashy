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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import dev.ishubhamsingh.splashy.core.navigation.TopLevelScreen
import dev.ishubhamsingh.splashy.core.presentation.SplashyTheme
import dev.ishubhamsingh.splashy.core.utils.KeyValueStorage
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.features.settings.Theme
import org.koin.compose.koinInject

val goBack = mutableStateOf(false)
val isDarkThemeState = mutableStateOf(false)

@Composable
fun App(darkTheme: Boolean, dynamicColor: Boolean) {

  val settingsUtils = koinInject<SettingsUtils>()
  LaunchedEffect(Unit) {
    isDarkThemeState.value = settingsUtils.isDarkTheme(darkTheme)
  }

  SplashyTheme(darkTheme = isDarkThemeState.value, dynamicColor = dynamicColor) {
    Navigator(
      screen = TopLevelScreen(),
      disposeBehavior =
        NavigatorDisposeBehavior(disposeNestedNavigators = false, disposeSteps = true)
    ) {
      SlideTransition(it)
    }
  }
}

fun updateTheme(theme: Int, isSystemDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit) {
  val isDarkTheme = if(theme == Theme.SYSTEM.value) {
    isSystemDarkTheme
  } else {
    theme == Theme.DARK.value
  }
  onThemeChanged(isDarkTheme)
}
