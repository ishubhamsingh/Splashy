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
package dev.ishubhamsingh.splashy.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowIosBack
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.core.utils.UpdateSystemBars
import dev.ishubhamsingh.splashy.features.home.HomeScreenModel
import dev.ishubhamsingh.splashy.features.settings.SettingsScreenModel
import dev.ishubhamsingh.splashy.features.settings.Theme
import dev.ishubhamsingh.splashy.goBack
import dev.ishubhamsingh.splashy.isDarkThemeState
import dev.ishubhamsingh.splashy.ui.components.GoBack
import dev.ishubhamsingh.splashy.updateTheme

class SettingsScreen : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val screenModel = getScreenModel<SettingsScreenModel>()
    UpdateSystemBars(
      statusBarColor = Color.Transparent,
      navigationBarColor = Color.Transparent,
      isDarkTheme = isDarkThemeState.value
    )

    val navigator = LocalNavigator.currentOrThrow
    val state = screenModel.state.collectAsState()
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = "Settings",
              style = MaterialTheme.typography.titleLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          },
          navigationIcon = {
            Icon(
              imageVector = EvaIcons.Outline.ArrowIosBack,
              contentDescription = "back",
              modifier = Modifier.clickable { navigator.pop() }
            )
          }
        )
      }
    ) { paddingValues ->
      Surface(modifier = Modifier.padding(paddingValues = paddingValues)) {
        Column(
          modifier = Modifier.fillMaxSize()
        ) {
          ThemeSettings(state.value.isDarkTheme) {
            isDarkThemeState.value = it
            screenModel.onEvent(SettingsEvent.OnThemeChange(it))
          }
        }
      }
    }

    if (goBack.value) {
      GoBack(navigator)
      goBack.value = false
    }
  }

  @Composable
  fun ThemeSettings(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "Dark Theme", style = MaterialTheme.typography.titleMedium)
      Switch(
        checked = isDarkTheme,
        onCheckedChange = onToggleTheme
      )
    }
  }

}
